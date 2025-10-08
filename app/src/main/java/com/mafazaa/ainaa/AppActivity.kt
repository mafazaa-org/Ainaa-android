package com.mafazaa.ainaa

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.mafazaa.ainaa.utils.Constants.contactSupportUrl
import com.mafazaa.ainaa.utils.Constants.joinUrl
import com.mafazaa.ainaa.utils.Constants.safeSearchUrl
import com.mafazaa.ainaa.utils.Constants.supportUrl
import com.mafazaa.ainaa.data.local.SharedPrefs
import com.mafazaa.ainaa.data.models.NetworkResult
import com.mafazaa.ainaa.domain.models.DnsProtectionLevel
import com.mafazaa.ainaa.domain.models.UpdateState
import com.mafazaa.ainaa.service.MyAccessibilityService
import com.mafazaa.ainaa.service.MyAccessibilityService.Companion.startAccessibilityService
import com.mafazaa.ainaa.service.MyVpnService
import com.mafazaa.ainaa.domain.models.AppInfo
import com.mafazaa.ainaa.ui.common.BottomBar
import com.mafazaa.ainaa.navigation.Screen
import com.mafazaa.ainaa.ui.common.TopBar
import com.mafazaa.ainaa.ui.common.OkDialog
import com.mafazaa.ainaa.ui.dialog.BlockAppDialog
import com.mafazaa.ainaa.ui.dialog.ConfirmBlockedDialog
import com.mafazaa.ainaa.ui.dialog.EnableProtectionDialog
import com.mafazaa.ainaa.ui.dialog.HowItWorksDialog
import com.mafazaa.ainaa.ui.dialog.PermissionDialog
import com.mafazaa.ainaa.ui.dialog.ReportProblemDialog
import com.mafazaa.ainaa.ui.protection.EnableProtectionScreen
import com.mafazaa.ainaa.ui.protection.ProtectionActivatedScreen
import com.mafazaa.ainaa.ui.support.SupportScreen
import com.mafazaa.ainaa.ui.theme.AinaaTheme
import com.mafazaa.ainaa.utils.MyLog
import com.mafazaa.ainaa.domain.models.PermissionState
import com.mafazaa.ainaa.utils.getAllApps
import com.mafazaa.ainaa.utils.hasAccessibilityPermission
import com.mafazaa.ainaa.utils.hasNotificationPermission
import com.mafazaa.ainaa.utils.hasOverlayPermission
import com.mafazaa.ainaa.utils.hasUsageStatsPermission
import com.mafazaa.ainaa.utils.hasVpnPermission
import com.mafazaa.ainaa.utils.installApk
import com.mafazaa.ainaa.utils.openUrl
import com.mafazaa.ainaa.utils.requestAccessibilityPermission
import com.mafazaa.ainaa.utils.requestDrawOverlaysPermission
import com.mafazaa.ainaa.utils.requestUsageStatsPermission
import com.mafazaa.ainaa.utils.requestVpnPermission
import com.mafazaa.ainaa.utils.shareFile
import com.mafazaa.ainaa.utils.startVpnService
import com.mafazaa.ainaa.viewmodels.AppViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.java.KoinJavaComponent.inject

// Sealed dialog state to manage all dialogs from a single source of truth
sealed interface DialogState {
    data object ReportProblem : DialogState
    data object FirstTime : DialogState
    data class Permission(val permission: PermissionState) : DialogState

    // Keeps Block Apps dialog open, and optionally shows a nested confirm dialog for a selected app
    data class BlockApps(val confirmApp: AppInfo? = null) : DialogState
    data object HowItWorks : DialogState
    data class EnableProtectionConfirm(val level: DnsProtectionLevel) :
        DialogState
}


class AppActivity : ComponentActivity() {
    private var vpnPermission by mutableStateOf(false)
    private var overlayPermission by mutableStateOf(false)
    private var usageStatsPermission by mutableStateOf(false)
    private var accessibilityPermission by mutableStateOf(false)
    private var notificationPermission by mutableStateOf(
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: AppViewModel = getViewModel()
        val sharedPrefs: SharedPrefs by inject(SharedPrefs::class.java)
        viewModel.loadInstalledApps(getAllApps())
        MyLog.i(TAG, "Opening app")
        //viewModel.handleUpdateStatus(this)
        refreshPermissionState()
        setContent {
            AinaaTheme {
                MainRoot(
                    viewModel = viewModel,
                    sharedPrefs = sharedPrefs,
                )
            }
        }

    }

    @Composable
    private fun MainRoot(
        viewModel: AppViewModel,
        sharedPrefs: SharedPrefs,
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val backStack = remember {
            mutableStateListOf(
                if (!MyAccessibilityService.isRunning) Screen.EnableProtection
                else Screen.ProtectionActivated
            )
        }
        val context = LocalContext.current
        val apps = viewModel.apps.collectAsState().value

        // Single dialog state drives all dialogs; initialize with FirstTime if needed
        var dialogState by remember {
            mutableStateOf<DialogState?>(if (MyApp.isFirstTime) DialogState.FirstTime else null)
        }

        // Centralized dialogs rendering
        when (val d = dialogState) {
            is DialogState.ReportProblem -> {
                ReportProblemDialog(
                    onClose = { dialogState = null },
                    onSubmit = { report ->
                        viewModel.submitReport(report) {
                            when (it) {
                                NetworkResult.Success -> {
                                    Toast.makeText(
                                        context,
                                        "تم إرسال التقرير بنجاح",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        context,
                                        "فشل في أرسال التقرير : ${'$'}{it.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                else -> {}
                            }
                        }
                        dialogState = null
                    }
                )
            }

            is DialogState.FirstTime -> {
                OkDialog(
                    title = "هذا إصدار تجريبي",
                    message = """
يرجى ملاحظة أن هذا التطبيق في مرحلة التجربة وقد يحتوي على بعض المشاكل.
برجاء إبلاغنا عن أي مشكلة تحدث معك، و انتظار الاصدارات القادمة المحسنة بإذن الله.
""".trimIndent(),
                    onDismiss = {
                        dialogState = null
                        MyApp.isFirstTime = false
                    }
                )
            }

            is DialogState.Permission -> {
                PermissionDialog(
                    permissionState = d.permission,
                    onDismiss = { dialogState = null },
                    onClick = {
                        grantPermission(d.permission)
                        dialogState = null
                    }
                )
            }

            is DialogState.BlockApps -> {
                BlockAppDialog(
                    onDismiss = { dialogState = null },
                    appStates = apps,
                    onBlockClick = { app ->
                        dialogState = DialogState.BlockApps(confirmApp = app)
                    }
                )
                if (d.confirmApp != null) {
                    ConfirmBlockedDialog(
                        app = d.confirmApp,
                        onDismiss = { dialogState = DialogState.BlockApps() },
                        onConfirm = {
                            viewModel.toggleAppSelection(it.packageName)
                            dialogState = DialogState.BlockApps()
                        }
                    )
                }
            }

            is DialogState.HowItWorks -> {
                HowItWorksDialog(
                    onDismiss = { dialogState = null },
                    onContactClicked = { context.openUrl(contactSupportUrl) },
                    onSafeSearchClicked = { context.openUrl(safeSearchUrl) },
                    image = "file:///android_asset/howToKnow.jpg".toUri()
                )
            }

            is DialogState.EnableProtectionConfirm -> {
                EnableProtectionDialog(
                    onConfirm = {
                        // Submit phone & activate

                        Toast.makeText(
                            context,
                            "تم تفعيل الحماية بنجاح",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.saveLevel(d.level)
                        startAccessibilityService()
                        context.startVpnService()
                        backStack.add(Screen.ProtectionActivated)
                        backStack.remove(Screen.EnableProtection)
                        dialogState = null

                    },
                    onDismiss = { dialogState = null }
                )
            }

            null -> {}
        }

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopBar(
                    supportUs = { backStack.add(Screen.Support) },
                    home = {
                        if (MyVpnService.isRunning) {
                            backStack.add(Screen.ProtectionActivated)
                        }
                    }
                )
            },
            bottomBar = {
                BottomBar(
                    modifier = Modifier,
                    appVersion = BuildConfig.VERSION_NAME,
                    androidVersion = Build.VERSION.RELEASE
                ) {
                    if (MyAccessibilityService.isRunning) {
                        backStack.add(Screen.ProtectionActivated)
                    }
                }
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) { innerPadding ->
            NavDisplay(
                modifier = Modifier.padding(innerPadding),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator()
                ),
                entryProvider = { key ->
                    when (key) {
                        Screen.ProtectionActivated -> NavEntry(key) {
                            ProtectionActivatedScreen(
                                onSupportClick = { backStack.add(Screen.Support) },
                                onBlockAppClick = { dialogState = DialogState.BlockApps() },
                                onReportClick = { dialogState = DialogState.ReportProblem },
                                onConfirmProtectionClick = { dialogState = DialogState.HowItWorks },
                                onUpdateClick = { updateStatus ->
                                    when (updateStatus) {
                                        UpdateState.Downloaded -> {
                                            context.installApk(viewModel.updateFile())
                                        }

                                        is UpdateState.Failed,
                                        UpdateState.NoUpdate,
                                            -> {
                                            viewModel.handleUpdateStatus()
                                        }

                                        else -> {
                                        }
                                    }

                                },
                                updateState = viewModel.updateState.value,
                            )
                        }

                        Screen.Support -> NavEntry(key) {
                            SupportScreen(
                                onSupportClick = { openUrl(supportUrl) },
                                onJoinClick = { openUrl(joinUrl) },
                                onShareLogFile = { this@AppActivity.shareFile(viewModel.getLogFile()) },
                                onStopBlocking = { startAccessibilityService(MyAccessibilityService.ACTION_STOP) },
                                onOpenScreenShotWindow = {
                                    viewModel.showScreenshotOverlay(true)

                                }
                            )
                        }

                        Screen.EnableProtection -> NavEntry(key) {
                            var selectedLevel by remember { mutableStateOf(DnsProtectionLevel.LOW) }

                            EnableProtectionScreen(
                                report = { dialogState = DialogState.ReportProblem },
                                enableProtection = { level: DnsProtectionLevel ->
                                    selectedLevel = level
                                    when {
                                        !vpnPermission -> dialogState =
                                            DialogState.Permission(PermissionState.Vpn)

                                        !notificationPermission -> dialogState =
                                            DialogState.Permission(PermissionState.Notification)

                                        !overlayPermission -> dialogState =
                                            DialogState.Permission(PermissionState.Overlay)

                                        !accessibilityPermission -> dialogState =
                                            DialogState.Permission(PermissionState.Accessibility)

                                        else -> dialogState = DialogState.EnableProtectionConfirm(
                                            level = selectedLevel,
                                        )
                                    }
                                },
                                selectedLevel = selectedLevel,
                            )
                        }
                    }
                }
            )
        }
    }

    private fun refreshPermissionState() {
        if (!notificationPermission) {
            notificationPermission = hasNotificationPermission()
        }
        if (!vpnPermission) {
            vpnPermission = hasVpnPermission()
        }
        if (!overlayPermission) {
            overlayPermission = hasOverlayPermission()
        }
        if (!usageStatsPermission) {
            usageStatsPermission = hasUsageStatsPermission()
        }
        if (!accessibilityPermission) {
            accessibilityPermission = hasAccessibilityPermission()
        }


    }


    override fun onResume() {
        super.onResume()
        refreshPermissionState()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        refreshPermissionState()
    }


    private fun grantPermission(permissionState: PermissionState) {
        when (permissionState) {
            PermissionState.Notification -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions(arrayOf(POST_NOTIFICATIONS), 0)
                }
            }

            PermissionState.Vpn -> requestVpnPermission()
            PermissionState.Overlay -> requestDrawOverlaysPermission()
            PermissionState.UsageStats -> requestUsageStatsPermission()
            PermissionState.Accessibility -> requestAccessibilityPermission()
            PermissionState.Granted -> {}
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }


}

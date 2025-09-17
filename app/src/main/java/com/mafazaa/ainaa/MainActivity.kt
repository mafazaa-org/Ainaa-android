package com.mafazaa.ainaa

import android.Manifest.permission.*
import android.content.*
import android.os.*
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.core.net.*
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.*
import com.mafazaa.ainaa.Constants.contactSupportUrl
import com.mafazaa.ainaa.Constants.joinUrl
import com.mafazaa.ainaa.Constants.safeSearchUrl
import com.mafazaa.ainaa.Constants.supportUrl
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.data.local.LocalData
import com.mafazaa.ainaa.data.remote.NetworkResult
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.service.*
import com.mafazaa.ainaa.service.MyAccessibilityService.Companion.startAccessibilityService
import com.mafazaa.ainaa.ui.*
import com.mafazaa.ainaa.ui.theme.*
import org.koin.androidx.viewmodel.ext.android.*
import org.koin.java.KoinJavaComponent.inject

// Sealed dialog state to manage all dialogs from a single source of truth
sealed interface DialogState {
    data object ReportProblem : DialogState
    data object FirstTime : DialogState
    data class Permission(val permission: PermissionState) : DialogState

    // Keeps Block Apps dialog open, and optionally shows a nested confirm dialog for a selected app
    data class BlockApps(val confirmApp: AppInfo? = null) : DialogState
    data object HowItWorks : DialogState
    data class EnableProtectionConfirm(val level: ProtectionLevel, val phone: String) : DialogState
}

class MainActivity: ComponentActivity() {
    private var vpnPermission by mutableStateOf(false)
    private var overlayPermission by mutableStateOf(false)
    private var usageStatsPermission by mutableStateOf(false)
    private var accessibilityPermission by mutableStateOf(false)
    private var notificationPermission by mutableStateOf(
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel = getViewModel()
        val localData: LocalData by inject(LocalData::class.java)
        viewModel.loadInstalledApps(getAllApps(this))
        Lg.i(TAG, "Opening app")
        //viewModel.handleUpdateStatus(this)
        refreshPermissionState()
        setContent {
            AinaaTheme {
                MainRoot(
                    viewModel = viewModel,
                    localData = localData,
                )
            }
        }

    }

    @Composable
    private fun MainRoot(
        viewModel: MainViewModel,
        localData: LocalData,
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
                    ConfirmDeleteDialog(
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
                        viewModel.submitPhoneNumber(d.phone) {
                            when (it) {
                                NetworkResult.Success -> {
                                    Toast.makeText(
                                        context,
                                        "تم تفعيل الحماية بنجاح",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    viewModel.savePhoneNumber(d.phone)
                                    viewModel.saveLevel(d.level)
                                    startAccessibilityService()
                                    localData.activatedVpn = true
                                    ScreenShotService.start(context)
                                    context.startVpnService()
                                    backStack.add(Screen.ProtectionActivated)
                                    backStack.remove(Screen.EnableProtection)
                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        context,
                                        "فشل في أرسال البيانات : ${'$'}{it.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                else -> {}
                            }
                        }
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
                    if (MyVpnService.isRunning) {
                        backStack.add(Screen.ProtectionActivated)
                    }
                }
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
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
                            )
                        }

                        Screen.Support -> NavEntry(key) {
                            SupportScreen(
                                onSupportClick = { openUrl(supportUrl) },
                                onJoinClick = { openUrl(joinUrl) },
                                onShareLogFile = { this@MainActivity.shareFile(viewModel.getLogFile()) },
                                onStopBlocking = { startAccessibilityService(MyAccessibilityService.ACTION_STOP) }
                            )
                        }

                        Screen.EnableProtection -> NavEntry(key) {
                            var selectedLevel by remember { mutableStateOf(ProtectionLevel.LOW) }
                            var phoneNumber by remember { mutableStateOf("") }

                            EnableProtectionScreen(
                                report = { dialogState = DialogState.ReportProblem },
                                enableProtection = { level: ProtectionLevel, phone: String ->
                                    selectedLevel = level
                                    phoneNumber = phone
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
                                            phone = phoneNumber
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

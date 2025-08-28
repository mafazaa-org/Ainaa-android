package com.mafazaa.ainaa.ui.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService.prepare
import android.os.Build
import android.os.Bundle
import android.os.Process.myUid
import android.provider.Settings.canDrawOverlays
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.mafazaa.ainaa.BuildConfig
import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.core.Constants.contactSupportUrl
import com.mafazaa.ainaa.core.Constants.joinUrl
import com.mafazaa.ainaa.core.Constants.safeSearchUrl
import com.mafazaa.ainaa.core.Constants.supportUrl
import com.mafazaa.ainaa.core.MyApp
import com.mafazaa.ainaa.core.getAllApps
import com.mafazaa.ainaa.core.installApk
import com.mafazaa.ainaa.core.openUrl
import com.mafazaa.ainaa.core.requestDrawOverlaysPermission
import com.mafazaa.ainaa.core.requestUsageStatsPermission
import com.mafazaa.ainaa.core.requestVpnPermission
import com.mafazaa.ainaa.core.shareLogFile
import com.mafazaa.ainaa.data.NetworkResult
import com.mafazaa.ainaa.data.repository_impl.view_models.MainViewModel
import com.mafazaa.ainaa.domain.model.AppInfo
import com.mafazaa.ainaa.domain.model.PermissionState
import com.mafazaa.ainaa.domain.model.ProtectionLevel
import com.mafazaa.ainaa.domain.model.UpdateState
import com.mafazaa.ainaa.service.MonitorService
import com.mafazaa.ainaa.service.MyVpnService
import com.mafazaa.ainaa.service.VpnKeepAliveService
import com.mafazaa.ainaa.ui.HowItWorksDialog
import com.mafazaa.ainaa.ui.OkDialog
import com.mafazaa.ainaa.ui.PermissionDialog
import com.mafazaa.ainaa.ui.ProtectionActivatedScreen
import com.mafazaa.ainaa.ui.components.BlockAppDialog
import com.mafazaa.ainaa.ui.components.BottomBar
import com.mafazaa.ainaa.ui.components.ConfirmDeleteDialog
import com.mafazaa.ainaa.ui.components.EnableProtectionDialog
import com.mafazaa.ainaa.ui.components.EnableProtectionScreen
import com.mafazaa.ainaa.ui.components.ReportProblemDialog
import com.mafazaa.ainaa.ui.components.Screen
import com.mafazaa.ainaa.ui.components.SupportScreen
import com.mafazaa.ainaa.ui.components.TopBar
import com.mafazaa.ainaa.ui.theme.AinaaTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel
import androidx.core.content.edit
import com.mafazaa.ainaa.core.Constants


class MainActivity: ComponentActivity() {
    var selectedLevel by
        mutableStateOf(ProtectionLevel.LOW)

    private var vpnPermission by mutableStateOf(false)
    private var overlayPermission by mutableStateOf(false)
    private var usageStatsPermission by mutableStateOf(false)
    private var notificationPermission by mutableStateOf(
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel = getViewModel()
        viewModel.loadInstalledApps(getAllApps(this))
        Lg.i(TAG, "Opening app")
        //viewModel.handleUpdateStatus(this)
        refreshPermissionState()
        setContent {
            AinaaTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val backStack = remember {
                    mutableStateListOf(
                        if (!MyVpnService.isRunning) Screen.EnableProtection
                        else Screen.ProtectionActivated
                    )
                }
                val context = LocalContext.current
                var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
                val apps = viewModel.apps.collectAsState().value

                var showReportDialog by remember { mutableStateOf(false) }
                if (showReportDialog) {
                    ReportProblemDialog(
                        onClose = { showReportDialog = false },
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
                                            "فشل في أرسال التقرير : ${it.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                    else -> {}
                                }
                            }
                            showReportDialog = false
                        })
                }
                var firstTimeDialog by remember { mutableStateOf(MyApp.isFirstTime) }
                if (firstTimeDialog) {
                    OkDialog(
                        title = "هذا إصدار تجريبي",
                        message = """
يرجى ملاحظة أن هذا التطبيق في مرحلة التجربة وقد يحتوي على بعض المشاكل.
برجاء إبلاغنا عن أي مشكلة تحدث معك، و انتظار الاصدارات القادمة المحسنة بإذن الله.
""".trimIndent(),
                        onDismiss = {
                            firstTimeDialog = false
                            MyApp.isFirstTime = false
                        }
                    )

                }
                var permissionState by remember { mutableStateOf<PermissionState?>(null) }
                if (permissionState != null) {
                    PermissionDialog(
                        permissionState = permissionState!!,
                        onDismiss = { permissionState = null },
                        onClick = {
                            grantPermission(permissionState!!)
                            permissionState = null
                        }
                    )
                }

                var showBlockAppsDialog by remember { mutableStateOf(false) }
                if (showBlockAppsDialog) {
                    BlockAppDialog(
                        onDismiss = { showBlockAppsDialog = false },
                        appStates = apps,
                        onBlockClick = {
                            selectedApp = it
                        }
                    )
                }
                if (selectedApp != null) {
                    ConfirmDeleteDialog(
                        app = selectedApp!!,
                        onDismiss = { selectedApp = null },
                        onConfirm = {
                            viewModel.toggleAppSelection(it.packageName)
                            selectedApp = null
                        })
                }
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                    topBar = {
                        TopBar(
                            supportUs = {
                                backStack.add(Screen.Support)
                            },
                            home = {
                                if (MyVpnService.isRunning) {
                                    backStack.add(Screen.ProtectionActivated)
                                }
                            }
                        )
                    }, bottomBar = {
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
                                    var showHowItWorks by remember {
                                        mutableStateOf(false)
                                    }
                                    if (showHowItWorks) {
                                        HowItWorksDialog(
                                            onDismiss = { showHowItWorks = false },
                                            onContactClicked = {
                                                context.openUrl(contactSupportUrl)
                                            },
                                            onSafeSearchClicked = {
                                                context.openUrl(safeSearchUrl)
                                            },
                                            image = "file:///android_asset/howToKnow.jpg".toUri()
                                        )
                                    }
                                    ProtectionActivatedScreen(
                                        onSupportClick = { backStack.add(Screen.Support) },
                                        onBlockAppClick = {
                                            if (MonitorService.isRunning.value) {
                                                showBlockAppsDialog = true
                                            } else if (
                                                !overlayPermission
                                            ) {
                                                permissionState = PermissionState.Overlay
                                            } else if (
                                                !usageStatsPermission
                                            ) {
                                                permissionState = PermissionState.UsageStats
                                            } else {
                                                startService(
                                                    Intent(
                                                        this,
                                                        MonitorService::class.java
                                                    ).apply {
                                                        action = MonitorService.ACTION_START
                                                    }
                                                )
                                                showBlockAppsDialog = true
                                            }
                                        },
                                        onReportClick = { showReportDialog = true },
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
                                        onConfirmProtectionClick = {
                                            showHowItWorks = true
                                        },
                                    )

                                }

                                Screen.Support -> NavEntry(key) {
                                    SupportScreen(
                                        onSupportClick = {
                                            openUrl(supportUrl)
                                        }, onJoinClick = {
                                            openUrl(joinUrl)
                                        },
                                        onShareLogFile = {
                                            this@MainActivity.shareLogFile(viewModel.getLogFile())
                                        })

                                }

                                Screen.EnableProtection -> NavEntry(key) {

                                    var phoneNumber by remember { mutableStateOf("") }
                                    var showConfirmationDialog by remember {
                                        mutableStateOf(
                                            false
                                        )
                                    }
                                    if (showConfirmationDialog) {
                                        EnableProtectionDialog(
                                            onConfirm = {
                                                showConfirmationDialog = false
                                                viewModel.submitPhoneNumber(phoneNumber, {
                                                    when (it) {
                                                        NetworkResult.Success -> {
                                                            Toast.makeText(
                                                                context,
                                                                "تم تفعيل الحماية بنجاح",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            viewModel.savePhoneNumber(
                                                                phoneNumber
                                                            )
                                                            viewModel.saveLevel(selectedLevel)

                                                            val prefs = getSharedPreferences(
                                                                Constants.VPN_SH_PREF_NAME, Context.MODE_PRIVATE)
                                                            prefs.edit {
                                                                putInt(
                                                                    Constants.VPN_SH_PREF_KEY,
                                                                    selectedLevel.ordinal
                                                                )
                                                            }
                                                            startVpnService()
                                                            backStack.add(Screen.ProtectionActivated)
                                                            backStack.remove(Screen.EnableProtection)
                                                        }

                                                        is NetworkResult.Error -> {
                                                            Toast.makeText(
                                                                context,
                                                                "فشل في أرسال البيانات : ${it.message}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }

                                                        else -> {}
                                                    }
                                                })
                                            },
                                            onDismiss = { showConfirmationDialog = false }
                                        )
                                    }
                                    EnableProtectionScreen(
                                        report = { showReportDialog = true },
                                        enableProtection = { level: ProtectionLevel, phone: String ->
                                            selectedLevel = level

                                            phoneNumber = phone
                                            if (!vpnPermission) {
                                                permissionState = PermissionState.Vpn
                                            } else if (!notificationPermission) {
                                                permissionState = PermissionState.Notification
                                            } else {
                                                showConfirmationDialog = true
                                            }
                                        },
                                        selectedLevel = selectedLevel,
                                    )
                                }

                            }
                        })
                }
            }
        }

    }

    private fun refreshPermissionState() {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        if (!notificationPermission) {
            notificationPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        this,
                        POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
        }
        if (!vpnPermission) {
            vpnPermission = hasVpnPermission()
        }
        if (!overlayPermission) {
            overlayPermission = canDrawOverlays(this)
        }
        if (!usageStatsPermission) {
            usageStatsPermission = appOps.checkOpNoThrow(
                OPSTR_GET_USAGE_STATS,
                myUid(),
                this.packageName
            ) == MODE_ALLOWED
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

    private fun startVpnService() {
        val intent = Intent(this, MyVpnService::class.java).apply {
            action = MyVpnService.ACTION_START
            putExtra(MyVpnService.EXTRA_LEVEL,selectedLevel.ordinal )
        }

        ContextCompat.startForegroundService(this, intent)
        MyVpnService.isRunning = true

        val keepAliveIntent = Intent(this, VpnKeepAliveService::class.java)
        ContextCompat.startForegroundService(this, keepAliveIntent)

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
            PermissionState.Granted -> {}
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }


}

fun Context.hasVpnPermission(): Boolean = prepare(this) == null



package com.mafazaa.ainaa

import android.Manifest.permission.*
import android.app.*
import android.app.AppOpsManager.*
import android.content.*
import android.content.pm.*
import android.net.VpnService.*
import android.os.*
import android.os.Process.*
import android.provider.Settings.*
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.core.content.*
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.*
import com.mafazaa.ainaa.Constants.joinUrl
import com.mafazaa.ainaa.Constants.supportUrl
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.service.*
import com.mafazaa.ainaa.ui.*
import com.mafazaa.ainaa.ui.theme.*
import org.koin.androidx.viewmodel.ext.android.*

class MainActivity: ComponentActivity() {
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
                    FirstTimeDialog(
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

                                        }, updateState = viewModel.updateState.value
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
                                    var selectedLevel by remember {
                                        mutableStateOf(ProtectionLevel.LOW)
                                    }
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
        }
        ContextCompat.startForegroundService(this, intent)
        MyVpnService.isRunning = true
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



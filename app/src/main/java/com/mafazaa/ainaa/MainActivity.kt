package com.mafazaa.ainaa

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.*
import android.app.AppOpsManager.*
import android.content.*
import android.content.pm.*
import android.net.*
import android.net.VpnService.*
import android.os.*
import android.os.Process.*
import android.provider.*
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
import androidx.compose.ui.unit.*
import androidx.core.content.*
import androidx.core.net.*
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.*
import com.mafazaa.ainaa.Constants.joinUrl
import com.mafazaa.ainaa.Constants.supportUrl
import com.mafazaa.ainaa.MyApp.Companion.getAllApps
import com.mafazaa.ainaa.MyApp.Companion.startMonitoring
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.services.*
import com.mafazaa.ainaa.ui.*
import com.mafazaa.ainaa.ui.theme.*
import org.koin.androidx.viewmodel.ext.android.*

class MainActivity: ComponentActivity() {
    private var permissionState by mutableStateOf(PermissionState.Vpn)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel = getViewModel()
        viewModel.loadInstalledApps(getAllApps(this))

        refreshPermissionState()
        setContent {
            ProtectMeTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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
                                        SubmitResult.Success -> {
                                            Toast.makeText(
                                                context,
                                                "تم إرسال التقرير بنجاح",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                        is SubmitResult.Error -> {
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
                    var showPermissionDialog by remember { mutableStateOf(false) }
                    if (showPermissionDialog) {
                        PermissionDialog(
                            permissionState = permissionState,
                            onDismiss = { showPermissionDialog = false },
                            onClick = {
                                grantPermission()
                                showPermissionDialog = false
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
                        topBar = {
                            TopBar(
                                canBlock = MyVpnService.isRunning,
                                blockSpecificApp = {
                                    showBlockAppsDialog = true
                                },
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
                            BottomBar(Modifier) {
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
                                            onBlockAppClick = { showBlockAppsDialog = true },
                                            onReportClick = { showReportDialog = true })
                                    }

                                    Screen.Support -> NavEntry(key) {
                                        SupportScreen(onSupportClick = {
                                            openUrl(supportUrl)
                                        }, onJoinClick = {
                                            openUrl(joinUrl)
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
                                                            SubmitResult.Success -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    "تم تفعيل الحماية بنجاح",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                                viewModel.savePhoneNumber(
                                                                    phoneNumber
                                                                )
                                                                startVpnService(selectedLevel)
                                                                startService(
                                                                    Intent(
                                                                        this,
                                                                        MyForegroundService::class.java
                                                                    )
                                                                )
                                                                startMonitoring()
                                                                backStack.add(Screen.ProtectionActivated)
                                                                backStack.remove(Screen.EnableProtection)
                                                            }

                                                            is SubmitResult.Error -> {
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
                                                if (permissionState != PermissionState.Granted) {
                                                    showPermissionDialog = true
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
    }

    private fun refreshPermissionState() {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager

        permissionState =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                PermissionState.Notification
            } else if (prepare(this) != null) {
                PermissionState.Vpn
            } else if (!canDrawOverlays(this)) {
                PermissionState.Overlay
            } else if (appOps.checkOpNoThrow(
                    OPSTR_GET_USAGE_STATS,
                    myUid(),
                    this.packageName
                ) != MODE_ALLOWED
            ) {
                PermissionState.UsageStats
            } else {
                PermissionState.Granted
            }

    }

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        startActivity(intent)
    }

    private fun prepareVpnService() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, VPN_REQUEST_CODE)
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

    private fun startVpnService(protectionLevel: ProtectionLevel) {
        val intent = Intent(this, MyVpnService::class.java).apply {
            action = MyVpnService.ACTION_START
            putExtra(MyVpnService.EXTRA_LEVEL, protectionLevel.ordinal)
        }

        ContextCompat.startForegroundService(this, intent)
        MyVpnService.isRunning = true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }

    companion object {
        private const val VPN_REQUEST_CODE = 100
    }

    private fun grantPermission() {
        when (permissionState) {
            PermissionState.Notification -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions(arrayOf(POST_NOTIFICATIONS), 0)
                }
            }

            PermissionState.Vpn -> prepareVpnService()
            PermissionState.Overlay -> requestDrawOverlaysPermission()
            PermissionState.UsageStats -> requestUsageStatsPermission()
            PermissionState.Granted -> {}
        }
    }

    private fun requestDrawOverlaysPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        startActivity(intent)
    }
}



package com.mafazaa.ainaa.service

import android.accessibilityservice.*
import android.annotation.*
import android.content.Context
import android.content.Intent
import android.view.accessibility.*
import com.mafazaa.ainaa.*
import com.mafazaa.ainaa.Lg.logUiTree
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.data.local.LocalData
import com.mafazaa.ainaa.model.BlockReason
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject

@SuppressLint("AccessibilityPolicy")
class MyAccessibilityService : AccessibilityService() {
    lateinit var overlay: OverlayManager
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    lateinit var overlayManager: OverlayManager
    private var blockedApps = emptySet<String>()
    private val localData: LocalData by inject(LocalData::class.java)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                isRunning = false
                return START_NOT_STICKY
            }

            ACTION_SHARE_CURRENT_SCREEN -> {
                if (!isRunning) {
                    Lg.w(TAG, "Service not running, cannot share screen")
                }
                shareFile(logUiTree(rootInActiveWindow))

            }

            else -> {//or ACTION_START
                isRunning = true
                return START_STICKY
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        overlayManager = inject<OverlayManager>(OverlayManager::class.java).value
        overlay =
            OverlayManager(this) // This seems redundant as overlayManager is already initialized.

        if (blockedApps.isEmpty() && isKeyguardSecure()) {
            blockedApps = localData.apps
            Lg.d(TAG, "Loaded blocked apps: ")


        }

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.flags = info.flags or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        serviceInfo = info
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (!isRunning)
            return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) return

        val pkg = event.packageName?.toString()

        val root = rootInActiveWindow ?: return
        if (checkBlockedApp(pkg)) {
            Lg.i(TAG, "Blocked app in use: $pkg")
            block(BlockReason.UsingBlockedApp(pkg ?: "unknown"))
            return
        }
        val hasLabel = containsText(root, getString(R.string.app_name))
        val hasPackageName = containsText(root, applicationContext.packageName)
        val hasDisableActions = hasAppInfoActionButtons(root)

        if (isInAppInfoScreen(root, pkg)) {
            Lg.d(TAG, "Dumping view hierarchy:\n${dumpTreeToString(root)}")
            block(BlockReason.TryingToDisable(rootInActiveWindow))
        }
        if (isUninstallScreen(root, pkg)) {
            Lg.d(TAG, "Dumping view hierarchy:\n${dumpTreeToString(root)}")
            block(BlockReason.TryingToDisable(rootInActiveWindow))
        }
        if (isInOverlayScreen(root, isLikelySettingsPackage(pkg))) {
            Lg.d(TAG, "Dumping view hierarchy:\n${dumpTreeToString(root)}")
            block(BlockReason.TryingToDisable(rootInActiveWindow))
        }
    }


    private fun containsText(root: AccessibilityNodeInfo, text: String): Boolean {
        val nodes = root.findAccessibilityNodeInfosByText(text)
        return nodes != null && nodes.isNotEmpty()
    }

    private fun isInAppInfoScreen(root: AccessibilityNodeInfo, pkg: String?): Boolean {
        if (!isLikelySettingsPackage(pkg)) {
            return false
        }
        if (!containsText(root, getString(R.string.app_name))) {
            return false
        }
        val hints = listOf("App info", "App details")
        for (h in hints) {
            val nodes = root.findAccessibilityNodeInfosByText(h)
            if (nodes != null && nodes.isNotEmpty()) return true
        }
        return false
    }

    private fun isUninstallScreen(root: AccessibilityNodeInfo, pkg: String?): Boolean {
        if (pkg != "com.google.android.packageinstaller")
            return false

        return containsText(root, getString(R.string.app_name))
    }

    private fun isInOverlayScreen(root: AccessibilityNodeInfo, isInSettings: Boolean): Boolean {
        if (!isInSettings) return false
        if (getNodesCount(root) != 15)//todo
            return false

        return containsText(root, getString(R.string.app_name))
    }

    private fun getNodesCount(root: AccessibilityNodeInfo): Int {
        var count = 0
        fun recurse(node: AccessibilityNodeInfo) {
            count++
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { recurse(it) }
            }
        }
        recurse(root)
        return count
    }


    private fun hasAppInfoActionButtons(root: AccessibilityNodeInfo): String? {
        val hints = listOf("Force stop", "Uninstall", "Accessibility")
        for (h in hints) {
            val nodes = root.findAccessibilityNodeInfosByText(h)
            if (nodes != null && nodes.isNotEmpty()) return h
        }
        return null
    }

    private fun isLikelySettingsPackage(pkg: String?): Boolean {
        if (pkg == null) return false
        val settingsPkgs = listOf(
            "com.android.settings",              // AOSP
            "com.samsung.android.settings",      // Samsung
            "com.miui.securitycenter",           // MIUI (may vary)
            "com.huawei.systemmanager"           // EMUI (may vary)
            // add vendor-specific packages you target
        )
        return settingsPkgs.any { pkg.startsWith(it) } || pkg.contains(
            "settings",
            ignoreCase = true
        )
    }

    private fun checkBlockedApp(currentApp: String?): Boolean {
        return currentApp != null &&
                currentApp in blockedApps
    }

    private fun block(reason: BlockReason) {
        serviceScope.launch(Dispatchers.Main) {
            overlayManager.showOverlay(reason)
        }
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    companion object {
        fun Context.startAccessibilityService(action: String = ACTION_START) {
            isRunning = true
            val intent = Intent(this, MyAccessibilityService::class.java).apply {
                this@apply.action = action
            }
            startService(intent)
        }

        val ACTION_STOP = "STOP_ACCESSIBILITY"
        val ACTION_START = "START_ACCESSIBILITY"
        val ACTION_SHARE_CURRENT_SCREEN = "SHARE_CURRENT_SCREEN"
        var isRunning = false
        const val TAG = "MyAccessibilityService"
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        isRunning = false
    }

    override fun onInterrupt() {
        serviceScope.cancel()
        Lg.w(TAG, "Service interrupted")
        isRunning = false

    }
}

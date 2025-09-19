package com.mafazaa.ainaa.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.Lg.logUiTree
import com.mafazaa.ainaa.R
import com.mafazaa.ainaa.data.local.LocalData
import com.mafazaa.ainaa.isKeyguardSecure
import com.mafazaa.ainaa.model.BlockReason
import com.mafazaa.ainaa.model.ScreenAnalyser
import com.mafazaa.ainaa.model.ScriptResult
import com.mafazaa.ainaa.model.repo.ScriptRepo
import com.mafazaa.ainaa.shareFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import kotlin.time.measureTimedValue

@SuppressLint("AccessibilityPolicy")
class MyAccessibilityService : AccessibilityService() {
    lateinit var overlay: OverlayManager
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    lateinit var overlayManager: OverlayManager
    private var blockedApps = emptySet<String>()
    private val localData: LocalData by inject(LocalData::class.java)
    private val scriptRepo: ScriptRepo by inject(ScriptRepo::class.java)
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
                serviceScope.launch {
                    val screenAnalysis = ScreenAnalyser.analyzeScreen(
                        rootInActiveWindow, getString(R.string.app_name)
                    )
                    shareFile(logUiTree("screenShot", screenAnalysis))
                }
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

        if (
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) return

        rootInActiveWindow?.let { rootInActiveWindow ->
            serviceScope.launch {
                val (screenAnalysis, t) = measureTimedValue {
                    ScreenAnalyser.analyzeScreen(rootInActiveWindow, getString(R.string.app_name))
                }
                Lg.d(
                    TAG,
                    "Screen analyzed in ${t.inWholeMilliseconds} nodes=${screenAnalysis.nodesCount}"
                )
                val pkg = screenAnalysis.pkg
                if (checkBlockedApp(pkg)) {
                    Lg.i(TAG, "Blocked app in use: $pkg")
                    block(BlockReason.UsingBlockedApp(pkg ?: "unknown"))
                    return@launch
                }
                val (res, t2) = measureTimedValue { scriptRepo.evaluate(screenAnalysis) }
                Log.d(TAG, "Script evaluated in ${t2.inWholeMilliseconds} ")
                when (res) {
                    is ScriptResult.Error -> {
                        Lg.e(TAG, "Script evaluation error: ${res.error}")

                    }

                    is ScriptResult.Success -> {
                        if (res.matched) {
                            Lg.i(
                                TAG,
                                "Blocking due to script match: ${res.scriptName} on ${screenAnalysis.pkg}"
                            )
                            block(BlockReason.TryingToDisable(res.scriptName, screenAnalysis))
                            return@launch
                        }
                    }
                }
            }
        }


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

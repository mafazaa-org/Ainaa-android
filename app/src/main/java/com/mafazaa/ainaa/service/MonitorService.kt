package com.mafazaa.ainaa.service

import android.app.*
import android.app.usage.*
import android.content.*
import android.graphics.*
import android.os.*
import android.util.*
import android.view.*
import com.mafazaa.ainaa.*
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.databinding.LockScreenLayoutBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.java.KoinJavaComponent.inject

class MonitorService: Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private val handler = Handler(Looper.getMainLooper())
    lateinit var usageStatsManager: UsageStatsManager
    private val localData: LocalData by inject(LocalData::class.java)
    private var lockOverlay: View? = null
    private val checkRunnable = MyRunnable()

    override fun onCreate() {
        super.onCreate()
        MyNotificationManager.startForegroundService(this)
        usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        startListeningToConfig()
        serviceScope.launch {
            isRunning
        }
        Lg.d(TAG, "Monitor Service created")

//        isMonitoringLive.observeForever {//todo lifecycle
//            if (it) {
//                handler.post(checkRunnable)
//            } else {
//                handler.removeCallbacks(checkRunnable)
//                closeHomeOverlay()
//            }
//        }

    }

    private fun startListeningToConfig() {
        //for future use
    }

    private fun closeHomeOverlay() {
        if (lockOverlay == null) return
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.removeView(lockOverlay)
        lockOverlay = null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                isRunning.value = true
                serviceScope.launch {
                    handler.post(checkRunnable)
                }
                Lg.d(TAG , "Monitoring started")
            }
            ACTION_STOP -> {
                isRunning.value = false
                serviceScope.launch {
                    handler.removeCallbacks(checkRunnable)
                    closeHomeOverlay()
                }
                Lg.d(TAG, "Monitoring stopped")
            }
            else -> {
                Lg.e(TAG, "Unknown action: ${intent?.action}")
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning.value= false
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getForegroundApp(): String? {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 15 // todo check
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )
        return usageStats
            ?.maxByOrNull { it.lastTimeUsed } // More concise way to get the most recent app
            ?.packageName
    }

    inner class MyRunnable: Runnable {
        override fun run() {
            serviceScope.launch {
                while (isRunning.value) {
                    checkAndRedirect()
                }
            }
        }
    }

    private fun showLockScreen() {
        if (lockOverlay != null) {
            return
        }
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,//todo
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )
        val binding = LockScreenLayoutBinding.inflate(LayoutInflater.from(this))
        binding.closeBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            closeHomeOverlay()
        }

        lockOverlay= binding.root

        windowManager.addView(lockOverlay, params)

    }



    private suspend fun checkAndRedirect() {
        val currentApp = getForegroundApp()
        val blockedApp = localData.apps
        if ((currentApp !in blockedApp || currentApp == null )&& isRunning.value ) {
            Log.d(TAG, "Allowed app: $currentApp")
        } else {
            serviceScope.launch (Dispatchers.Main){
            showLockScreen()  }
            Lg.d(
                TAG,
                "Blocked app: $currentApp "
            )
        }
        delay(Constants.CHECK_INTERVAL)
    }
    companion object {
        const val TAG = "MyForegroundService"
        var isRunning = MutableStateFlow(false)
        const val ACTION_START ="START"
        const val ACTION_STOP = "STOP"

    }

}
/*

    private fun listenToConfig(id: String) {
        MyApp.firebaseRepo.listenToConfig(id, { childType, allowedApps ->
            serviceScope.launch {
                ChildPreferences.saveChild(
                    this@MyForegroundService,
                    ChildPreferences.getChild(this@MyForegroundService)!!.copy(typeName = childType)
                )
                ChildPreferences.saveAllowedApps(this@MyForegroundService, allowedApps)
            }
        }, {})
    }

    private fun listenToPause(id: String) {
        MyApp.firebaseRepo.listenForPauseStatus(id, {
            if (it) {
                stopMonitoring()
            } else {
                startMonitoring()
            }
        }, {
            MyApp.crashlytics.log("Failed to listen for pause status")
        })
    }
 */

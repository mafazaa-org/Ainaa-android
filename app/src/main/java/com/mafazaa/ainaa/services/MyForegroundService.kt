package com.mafazaa.ainaa.services

import android.app.*
import android.app.usage.*
import android.content.*
import android.graphics.*
import android.os.*
import android.util.*
import android.view.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mafazaa.ainaa.*
import com.mafazaa.ainaa.MyApp.Companion.stopMonitoring
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.databinding.LockScreenLayoutBinding
import com.mafazaa.ainaa.ui.*
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject

class MyForegroundService: Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private val handler = Handler(Looper.getMainLooper())
    lateinit var usageStatsManager: UsageStatsManager
    private val localData: LocalData by inject(LocalData::class.java)
    private var lockOverlay: View? = null

    override fun onCreate() {
        super.onCreate()
        MyNotificationManager.createNotificationChannel(this)
        MyNotificationManager.startForegroundService(this)
        usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        /*        serviceScope.launch {
                    val child = ChildPreferences.getChild(this@MyForegroundService)
                    if (child == null) {
                        MyApp.crashlytics.log("Child is null")
                        return@launch
                    }

                    serviceScope.launch {
                        listenToPause(child.id.toString())
                        listenToConfig(child.id.toString())
                        if (ChildPreferences.getChild(this@MyForegroundService)?.isControlled == true) {
                            startMonitoring()
                        } else {
                            stopMonitoring()
                        }
                    }
                }*/
        MyApp.isMonitoringLive.observeForever {//todo lifecycle
            if (it) {
                handler.post(checkRunnable)
            } else {
                handler.removeCallbacks(checkRunnable)
                closeHomeOverlay()
            }
        }

    }

    private fun closeHomeOverlay() {
        if (lockOverlay == null) return
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.removeView(lockOverlay)
        lockOverlay = null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMonitoring()
        serviceScope.cancel()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getForegroundApp(): String? {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 15 // Increased time window to 10 seconds
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
                while (MyApp.isMonitoring) {
                    checkAndRedirect()
                }
            }
        }
    }

    private fun showLockScreen() {
        if (lockOverlay != null) {
            return
        }
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
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
        if (currentApp !in blockedApp || currentApp == null || !MyApp.isMonitoring) {
            Log.d("MyForegroundService", "Allowed app: $currentApp")
        } else {
            serviceScope.launch (Dispatchers.Main){
            showLockScreen()  }
            Log.d(
                "MyForegroundService",
                "Blocked app: $currentApp "
            )
        }
        delay(Constants.CHECK_INTERVAL)
    }

    private val checkRunnable = MyRunnable()


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

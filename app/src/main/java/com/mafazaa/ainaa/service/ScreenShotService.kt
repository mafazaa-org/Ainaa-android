package com.mafazaa.ainaa.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mafazaa.ainaa.service.MyAccessibilityService.Companion.startAccessibilityService
import com.mafazaa.ainaa.ui.service.ScreenshotOverlay
import com.mafazaa.ainaa.ui.theme.AinaaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScreenShotService : Service() {
    //todo make it like
    private lateinit var windowManager: WindowManager
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var overlayView: View? = null

    // Lifecycle + saved state to satisfy Compose outside Activity/Fragment
    private val myLifecycleOwner = MyLifecycleOwner()
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        myLifecycleOwner.onStart()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        showOverlay()
        return START_NOT_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showOverlay() {
        Log.d(TAG, "showOverlay")
        if (overlayView != null) return
        myLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        myLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(myLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(myLifecycleOwner)
            setContent {
                AinaaTheme {
                    ScreenshotOverlay(
                        modifier = Modifier.pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    val layoutParams =
                                        overlayView!!.layoutParams as WindowManager.LayoutParams
                                    layoutParams.x += dragAmount.x.toInt()
                                    layoutParams.y += dragAmount.y.toInt()
                                    windowManager.updateViewLayout(overlayView, layoutParams)
                                    change.consume()
                                }
                            )
                        },
                        onClose = { closeOverlay() },
                        onScreenShot = { d ->
                            serviceScope.launch {
                                delay(d)
                                startAccessibilityService(MyAccessibilityService.ACTION_SHARE_CURRENT_SCREEN)
                            }
                        })
                }
            }
        }


        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = initialX
            y = initialY
        }
        windowManager.addView(overlayView, params)

    }

    private fun closeOverlay() {
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
        myLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        myLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }


    companion object {
        private const val initialX = -100
        private const val initialY = 800

        const val TAG = "ComposeOverlayService"
        fun start(context: Context) {
            context.startService(Intent(context, ScreenShotService::class.java))
        }
    }
}


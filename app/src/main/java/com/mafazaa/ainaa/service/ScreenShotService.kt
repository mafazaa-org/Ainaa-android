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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mafazaa.ainaa.service.MyAccessibilityService.Companion.startAccessibilityService
import com.mafazaa.ainaa.ui.service.ScreenshotOverlay
import com.mafazaa.ainaa.ui.theme.AinaaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScreenShotService : Service(), LifecycleOwner, SavedStateRegistryOwner {
    private lateinit var windowManager: WindowManager
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var overlayView: View? = null
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    // Lifecycle + saved state to satisfy Compose outside Activity/Fragment
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateController = SavedStateRegistryController.create(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateController.savedStateRegistry
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        savedStateController.performAttach()
        savedStateController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        showOverlay()
        return START_NOT_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showOverlay() {
        Log.d(TAG, "showOverlay")
        if (overlayView != null) return
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@ScreenShotService)
            setViewTreeSavedStateRegistryOwner(this@ScreenShotService)
            setContent {
                AinaaTheme {
                    ScreenshotOverlay(
                        modifier = Modifier.pointerInput(Unit) {
                            var lastPosition = Offset.Zero
                            detectDragGestures(
                                onDragStart = { offset -> lastPosition = offset },
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
                        onScreenShot = {
                            serviceScope.launch {
                                delay(2000)
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
            x = -100
            y = 800
        }
        windowManager.addView(overlayView, params)

    }

    private fun closeOverlay() {
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    companion object {
        const val TAG = "ComposeOverlayService"
        var isOverlayShown = false//todo
        fun start(context: Context) {
            context.startService(Intent(context, ScreenShotService::class.java))
            isOverlayShown = true
        }
    }
}


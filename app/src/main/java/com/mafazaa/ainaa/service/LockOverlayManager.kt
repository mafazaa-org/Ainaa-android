package com.mafazaa.ainaa.service

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.model.BlockReason
import com.mafazaa.ainaa.shareFile
import com.mafazaa.ainaa.ui.service.LockScreenOverlay
import com.mafazaa.ainaa.ui.theme.AinaaTheme
import kotlinx.coroutines.flow.MutableStateFlow

class LockOverlayManager(val context: Context) {
    private var lockOverlay: View? = null

    val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    private var myLifecycleOwner: MyLifecycleOwner? = null
    fun closeOverlay() {
        if (lockOverlay == null) return
        myLifecycleOwner?.onDestroy()
        windowManager.removeView(lockOverlay)
        myLifecycleOwner = null
        lockOverlay = null
        isShowing.value = false
    }

    fun showOverlay(reason: BlockReason) {

        if (lockOverlay != null) {
            return
        }
        myLifecycleOwner = MyLifecycleOwner()
        myLifecycleOwner?.onStart()

        lockOverlay = ComposeView(context).apply {
            setViewTreeLifecycleOwner(myLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(myLifecycleOwner)
            setContent {
                AinaaTheme {
                    LockScreenOverlay(reason = reason, onClose = {
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_HOME)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        closeOverlay()
                    }, onShareLog = {
                        closeOverlay()
                        when (reason) {
                            is BlockReason.TryingToDisable -> {
                                context.shareFile(
                                    Lg.logUiTree(
                                        reason.codeName,
                                        reason.screenAnalysis
                                    )
                                )
                            }

                            is BlockReason.UsingBlockedApp -> {
                                context.shareFile(Lg.logFalseBlockedApp(reason.packageName))
                            }
                        }
                    })
                }
            }
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,//todo
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(lockOverlay, params)
        isShowing.value = true

    }


    companion object {
        const val TAG = "OverlayManager"

        var isShowing = MutableStateFlow(false)


    }

}
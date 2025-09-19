package com.mafazaa.ainaa.service

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.databinding.LockScreenLayoutBinding
import com.mafazaa.ainaa.model.BlockReason
import com.mafazaa.ainaa.shareFile
import kotlinx.coroutines.flow.MutableStateFlow

class OverlayManager(val context: Context) {
    private var lockOverlay: View? = null

    val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

    fun closeOverlay() {
        if (lockOverlay == null) return
        windowManager.removeView(lockOverlay)
        lockOverlay = null
        isShowing.value = false
    }

    fun showOverlay(reason: BlockReason) {
        if (lockOverlay != null) {
            return
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
        val binding = LockScreenLayoutBinding.inflate(LayoutInflater.from(context))
        binding.reasonTextView.text = reason.getName() // Set the reason text
        binding.closeBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            closeOverlay()
        }
        binding.shareLogTv.setOnClickListener {
            closeOverlay()
            when(reason){
                is BlockReason.TryingToDisable -> {
                    context.shareFile(Lg.logUiTree(reason.codeName,reason.screenAnalysis))
                }
                is BlockReason.UsingBlockedApp -> {
                    context.shareFile(Lg.logWithInfo(reason.packageName))
                }
            }

        }

        lockOverlay = binding.root

        windowManager.addView(lockOverlay, params)
        isShowing.value = true

    }


    companion object {
        const val TAG = "OverlayManager"

        var isShowing = MutableStateFlow(false)


    }

}
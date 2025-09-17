package com.mafazaa.ainaa.model

import android.view.accessibility.AccessibilityNodeInfo

sealed class BlockReason {
    abstract fun getName(): String
    data class UsingBlockedApp(val packageName: String) : BlockReason() {
        override fun getName(): String {
            return "تطبيق محظور"
        }
    }

    data class TryingToDisable(val root: AccessibilityNodeInfo) : BlockReason() {
        override fun getName(): String {
            return "محاولة تعطيل المراقبة"
        }
    }
}
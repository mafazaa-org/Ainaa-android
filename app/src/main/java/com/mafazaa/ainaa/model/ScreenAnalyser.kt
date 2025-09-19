package com.mafazaa.ainaa.model

import android.view.accessibility.AccessibilityNodeInfo
import com.mafazaa.ainaa.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ScreenAnalyser {
    const val UNKNOWN_PACKAGE = "unknown"
    suspend fun analyzeScreen(root: AccessibilityNodeInfo, appName: String): ScreenAnalysis =
        withContext(Dispatchers.Default){
        var hasAppName = false
        var nodesCount = 0
        fun toScreenNode(node: AccessibilityNodeInfo): ScreenNode {
            nodesCount++
            if (!hasAppName && node.text?.contains(appName, true)==true) {
                hasAppName = true
            }
            val children = mutableListOf<ScreenNode>()
            for (i in 0 until node.childCount) {
                val c =node.getChild(i)?:continue
                children.add(toScreenNode(c))
            }
            return ScreenNode(
                cls = node.className?.toString(),
                text = node.text?.toString(),
                id = node.viewIdResourceName,
                desc = node.contentDescription?.toString(),
                children = children
            )
        }

        val screenNode = toScreenNode(root)

        return@withContext ScreenAnalysis(
            pkg = root.packageName?.toString()?:UNKNOWN_PACKAGE,
            appName = appName,
            nodesCount = nodesCount,
            root = screenNode,
            hasAppName = hasAppName,
            isSettingsScreen = isLikelySettingsPackage(root.packageName?.toString())
        )
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
}
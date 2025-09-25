package com.mafazaa.ainaa

import com.mafazaa.ainaa.model.ScriptCode

object Constants {
    const val vpnAddress = "10.0.0.2"
    const val supportUrl = "https://ainaa.mafazaa.com/support_us"
    const val joinUrl = "https://www.mafazaa.com/join"
    const val contactSupportUrl = "https://ainaa.mafazaa.com/support"
    const val safeSearchUrl = "https://google.com/safesearch"

    /**
     * See [com.mafazaa.ainaa.data.remote.KtorRepo.getLatestVersion]
     */
    const val releaseApkName = "ainaa"
    const val maxNodes = 1000//todo if screen analysis exceeds this value stop analyzing

    /**
     * Default script codes to detect disabling attempts
     */
    val defaultCodes: List<ScriptCode> = listOf(
        ScriptCode(
            "uninstall screen xiaomi", """          
            (function() {
              try {
                var pkg = (screen && screen.pkg) || null;
                return screen.hasAppName && pkg === "com.google.android.packageinstaller" ;
              } catch (e) {
                return false;
              }
            })();
        """.trimIndent()
        ),
        ScriptCode(
            "Overlay screen xiaomi", """          
(function() {               
    try {
        return screen.hasAppName && screen.isSettingsScreen && screen.nodesCount==15;
    } catch (e) {
        return false;
    }
})();
        """.trimIndent()
        ), ScriptCode(
            "app info screen xiaomi", """          
(function() {     
            try {
            if (!screen.hasAppName || !screen.isSettingsScreen )
                return false;
ls= ["App info", "App details"];
var hasHint=false;
for (h in ls) {
    hasHint= hasHint || containsText(screen.root, h);
}
      
    return hasHint;
  } catch (e) {
    return false;
  }
})();
        """.trimIndent()
        ), ScriptCode(
            "battery pop up xiaomi", """          
(function() {     
            try {
        if (!screen.root) return false;
        const children = screen.root.children;
        if (!children || children.length !== 5) return false;
        return children[0].cls === "android.widget.TextView" 
            && children[1].cls === "android.widget.TextView" 
            && children[2].cls === "android.widget.CheckBox" 
            && children[3].cls === "android.widget.Button"   
            && children[4].cls === "android.widget.Button"   
  } catch (e) {
    return false;
  }
})();
        """.trimIndent()
        )
    )
}
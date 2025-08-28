package com.mafazaa.ainaa

import android.content.*
import android.content.pm.*
import android.net.*
import android.os.*
import android.provider.*
import androidx.activity.*
import androidx.core.content.*
import androidx.core.net.*
import com.mafazaa.ainaa.model.*
import java.io.*

fun Context.installApk(apkFile: File) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    val apkUri =
        FileProvider.getUriForFile(
            this,
            "${this.packageName}.provider",
            apkFile
        )
    intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
    try {
        this.startActivity(intent)
    } catch (e: Exception) {
        Lg.e("InstallApk", "Error starting install", e)
    }
}

fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    intent.addCategory(Intent.CATEGORY_BROWSABLE)
    startActivity(intent)
}

fun ComponentActivity.requestDrawOverlaysPermission() {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    startActivity(intent)
}

fun ComponentActivity.requestVpnPermission() {
    val intent = VpnService.prepare(this)
    if (intent != null) {
        startActivityForResult(intent, 0)
    }
}

fun ComponentActivity.requestUsageStatsPermission() {
    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    startActivity(intent)
}

fun Context.shareLogFile(logFile:File) {
    val uri = FileProvider.getUriForFile(
        this,
        "${packageName}.provider",
        logFile
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    startActivity(Intent.createChooser(shareIntent, "Share log file"))
}

fun getAllApps(context: Context): List<AppInfo> {
    val apps = mutableListOf<AppInfo>()
    val packageManager = context.packageManager
    val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    for (applicationInfo in packages) {
        if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) { // User app
            val name = packageManager.getApplicationLabel(applicationInfo).toString()
            val icon = packageManager.getApplicationIcon(applicationInfo)
            apps.add(AppInfo(name, icon, applicationInfo.packageName))
        }
    }
    return apps
}
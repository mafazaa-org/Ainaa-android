package com.mafazaa.ainaa.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File

fun Context.installApk( apkFile: File) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    val apkUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(
            this,
            "${this.packageName}.provider",
            apkFile
        )
    } else {
        Uri.fromFile(apkFile)
    }
    intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
    try {
        this.startActivity(intent)
    } catch (e: Exception) {
        Log.e("InstallApk", "Error starting install", e)
    }
}

fun Context.updateFile()= File(
    cacheDir,
    "update.apk"
)
fun ComponentActivity.openUrl( url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    intent.addCategory(Intent.CATEGORY_BROWSABLE)
    startActivity(intent)
}
fun ComponentActivity.requestDrawOverlaysPermission( ) {
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
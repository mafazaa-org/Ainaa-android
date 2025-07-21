package com.mafazaa.ainaa

import android.app.*
import android.content.*
import android.content.pm.*
import android.util.*
import com.mafazaa.ainaa.model.*

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any global state or libraries here
    }

    companion object {
        lateinit var instance: MyApp
            private set
    }

    fun getAppInfo(context: Context, packageName: String): AppInfo? {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val name = packageManager.getApplicationLabel(applicationInfo).toString()
            val icon = packageManager.getApplicationIcon(packageName)
            AppInfo(name, icon, packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("HomeActivity", "Error in getAppInfo with package: $packageName")
            null
        }
    }

    init {
        instance = this
    }
}
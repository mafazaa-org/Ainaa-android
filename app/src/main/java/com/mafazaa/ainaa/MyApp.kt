package com.mafazaa.ainaa

import android.app.*
import android.content.*
import android.content.pm.*
import android.util.*
import androidx.lifecycle.*
import com.mafazaa.ainaa.data.RemoteRepo
import com.mafazaa.ainaa.model.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.*
import org.koin.core.context.GlobalContext.startKoin
import java.io.File

class MyApp: Application() {
private lateinit var remoteRepo: RemoteRepo
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
        remoteRepo = getKoin().get<RemoteRepo>()
        // firebaseRepo = FirebaseImpl()
        // crashlytics = FirebaseCrashlytics.getInstance()
        isMonitoringLive.observeForever {
            isMonitoring = it
            Log.d("MyApp", "Monitoring is $it")
        }

    }


    companion object {


        var isMonitoringLive = MutableLiveData(false)
        var isMonitoring = false
            private set


        fun startMonitoring() {
            if (isMonitoring) return
            isMonitoringLive.postValue(true)
        }

        fun stopMonitoring() {
            isMonitoringLive.postValue(false)
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
    }



}
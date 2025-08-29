package com.mafazaa.ainaa

import android.app.*
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.work.*
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.receiver.BootReceiver
import com.mafazaa.ainaa.service.*
import org.koin.android.ext.android.*
import org.koin.android.ext.koin.*
import org.koin.core.context.GlobalContext.startKoin
import java.util.concurrent.*

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
        val localData: LocalData = getKoin().get()
        val fileRepo: FileRepo = getKoin().get()
        Lg.fileRepo = fileRepo // set the file repo for logging
        // firebaseRepo = FirebaseImpl()
        // crashlytics = FirebaseCrashlytics.getInstance()
        if (localData.lastVersion == 0) {//first run
            isFirstTime=true
            Lg.i(TAG, "First run, initializing local data")
            localData.lastVersion = BuildConfig.VERSION_CODE
        } else if (localData.lastVersion < BuildConfig.VERSION_CODE) {// app updated
            Lg.i(
                TAG,
                "App updated from version ${localData.lastVersion} to ${BuildConfig.VERSION_CODE}"
            )
            localData.lastVersion = BuildConfig.VERSION_CODE
            localData.downloadedVersion = 0// reset downloaded version
        } else {
            Lg.i(TAG, "App version is up to date: ${localData.lastVersion}")
        }
        val filter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
        ContextCompat.registerReceiver(this, BootReceiver(), filter,    ContextCompat.RECEIVER_EXPORTED)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        // val dailyWorkRequest =
        //     PeriodicWorkRequestBuilder<DailyNotificationWorker>(1, TimeUnit.DAYS) // minimum interval for testing
        //         .setConstraints(constraints)
        //         .build()

        // WorkManager.getInstance(this)
        //     .enqueueUniquePeriodicWork(
        //         "DailyNotificationWork",
        //         ExistingPeriodicWorkPolicy.KEEP,
        //         dailyWorkRequest
        //     )
    }

    companion object {
        private const val TAG = "MyApp"
        var isFirstTime= false
    }


}
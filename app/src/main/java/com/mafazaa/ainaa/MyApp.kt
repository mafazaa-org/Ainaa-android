package com.mafazaa.ainaa

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.mafazaa.ainaa.data.local.LocalData
import com.mafazaa.ainaa.model.FileRepo
import com.mafazaa.ainaa.receiver.BootReceiver
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }

        val fileRepo: FileRepo = getKoin().get()
        Lg.fileRepo = fileRepo // set the file repo for logging
        // firebaseRepo = FirebaseImpl()
        // crashlytics = FirebaseCrashlytics.getInstance()

        if (!isKeyguardSecure()) {
            val localData: LocalData = getKoin().get()
            if (localData.lastVersion == 0) {//first run
                isFirstTime = true
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
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BOOT_COMPLETED)
            addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED)
            addAction(Intent.ACTION_REBOOT)
        }
        ContextCompat.registerReceiver(
            this,
            BootReceiver(),
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )

    }

    companion object {
        private const val TAG = "MyApp"
        var isFirstTime = false
    }


}
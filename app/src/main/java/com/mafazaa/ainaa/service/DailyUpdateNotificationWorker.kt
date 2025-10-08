package com.mafazaa.ainaa.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mafazaa.ainaa.BuildConfig
import com.mafazaa.ainaa.utils.MyLog
import com.mafazaa.ainaa.data.local.SharedPrefs
import com.mafazaa.ainaa.domain.repo.UpdateRepo
import com.mafazaa.ainaa.helpers.MyNotificationManager.showUpdateNotification
import org.koin.java.KoinJavaComponent.inject

class DailyUpdateNotificationWorker(
    val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    val updateManager: UpdateRepo by inject(UpdateRepo::class.java)
    val sharedPrefs: SharedPrefs by inject(SharedPrefs::class.java)


    override suspend fun doWork(): Result {
        MyLog.d(TAG, "DailyNotificationWorker")
        updateManager.checkAndDownloadIfNeeded(BuildConfig.VERSION_CODE).collect {
        }
        if (sharedPrefs.downloadedVersion >= BuildConfig.VERSION_CODE) {
            showUpdateNotification(
                context
            )
        }

        return Result.success()
    }

    companion object {
        const val TAG = "DailyNotificationWorker"
    }

}

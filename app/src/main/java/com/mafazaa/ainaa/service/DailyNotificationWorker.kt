package com.mafazaa.ainaa.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mafazaa.ainaa.BuildConfig
import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.data.local.LocalData
import com.mafazaa.ainaa.model.repo.UpdateRepo
import com.mafazaa.ainaa.service.MyNotificationManager.showUpdateNotification
import org.koin.java.KoinJavaComponent.inject

class DailyNotificationWorker(
    val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    val updateManager: UpdateRepo by inject(UpdateRepo::class.java)
    val localData: LocalData by inject(LocalData::class.java)


    override suspend fun doWork(): Result {
        Lg.d(TAG, "DailyNotificationWorker")
        updateManager.checkAndDownloadIfNeeded(BuildConfig.VERSION_CODE).collect {
        }
        if (localData.downloadedVersion >= BuildConfig.VERSION_CODE) {
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

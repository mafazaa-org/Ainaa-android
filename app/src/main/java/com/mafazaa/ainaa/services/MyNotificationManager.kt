package com.mafazaa.ainaa.services

import android.app.*
import android.content.*
import android.os.*
import com.mafazaa.ainaa.R

object MyNotificationManager {
    const val CHANNEL_ID = "ainaa"
    const val NOTIFICATION_ID = 1
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "ainaa",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    fun startForegroundService(service: Service) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channelId = "ainaa"
        val notification = Notification.Builder(service, channelId)
            .setContentTitle("عينا سلسبيلا")
            .setContentText("الحماية مفعلة")
            .setSmallIcon(R.drawable.ic_auto_protect)
            .setOngoing(true)
            .build()
        service.startForeground(NOTIFICATION_ID, notification)
    }
}
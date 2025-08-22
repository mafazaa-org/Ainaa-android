package com.mafazaa.ainaa.service

import android.app.*
import android.content.*
import android.os.*
import androidx.core.app.*
import com.mafazaa.ainaa.*

object MyNotificationManager {
    const val SERVICE_CHANNEL_ID = "ainaa"
    const val UPDATE_CHANNEL_ID = "ainaa_update"
    const val SERVICE_ID = 1
    const val UPDATE_ID = 2
    var notificationChannelCreated = false
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val serviceChannel = NotificationChannel(
            SERVICE_CHANNEL_ID,
            SERVICE_CHANNEL_ID,
            NotificationManager.IMPORTANCE_NONE
        )
        val updateChannel = NotificationChannel(
            UPDATE_CHANNEL_ID,
            UPDATE_CHANNEL_ID,
            NotificationManager.IMPORTANCE_HIGH
        )
        updateChannel.description = "تحديثات التطبيق"
        serviceChannel.description = "خدمة الحماية من عيناً سلسبيلا"
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
        manager?.createNotificationChannel(updateChannel)
        notificationChannelCreated = true
    }


    fun startForegroundService(service: Service) {
        if (!notificationChannelCreated) {
            createNotificationChannels(service)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channelId = SERVICE_CHANNEL_ID
        val notification = Notification.Builder(service, channelId)
            .setContentTitle("عينا سلسبيلا")
            .setContentText("الحماية مفعلة")
            .setSmallIcon(R.drawable.ic_auto_protect)
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        service.startForeground(SERVICE_ID, notification)
    }

    fun showUpdateNotification(context: Context) {
        if (!notificationChannelCreated) {
            createNotificationChannels(context)
        }
        val notification = NotificationCompat.Builder(context, UPDATE_CHANNEL_ID)
            .setContentTitle("إصدار جديد متوفر")
            .setContentText("هناك إصدار جديد من تطبيق عيناً متوفر، يرجى التحديث إلى آخر إصدار لتحسين تجربتك.")
            .setSmallIcon(R.drawable.ic_red)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(UPDATE_ID, notification)
    }
}
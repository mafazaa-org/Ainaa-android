package com.example.protectme

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat

class MyVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    companion object {
        const val ACTION_START = "START_VPN"
        const val ACTION_STOP = "STOP_VPN"
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startVpn()
                return START_STICKY
            }

            ACTION_STOP -> {
                stopVpn()
                return START_NOT_STICKY
            }

            else -> {
                startVpn()
                return START_STICKY
            }
        }
    }

    private fun createNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NotificationUtils.CHANNEL_ID)
            .setContentTitle(getString(R.string.vpn_notification_title))
            .setContentText(getString(R.string.vpn_notification_content))
            .setSmallIcon(android.R.drawable.ic_lock_lock).setContentIntent(pendingIntent)
            .setOngoing(true).setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).build()

        startForeground(NotificationUtils.NOTIFICATION_ID, notification)
    }

    private fun startVpn() {
        val emptyIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = Builder().apply {
            addAddress("10.0.0.2", 32)
            addDnsServer("15.184.147.40")
            addDnsServer("15.184.182.221")
            setSession("SafeDNS")
            setBlocking(true)
            setConfigureIntent(emptyIntent) // منع إيقاف الخدمة من الإشعار
            setMtu(1500)
        }

        vpnInterface?.close()
        vpnInterface = builder.establish()
        isRunning = true
    }

    private fun stopVpn() {
        vpnInterface?.close()
        vpnInterface = null
        isRunning = false
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRunning) {
            // إعادة التشغيل التلقائي
            val intent = Intent(this, MyVpnService::class.java).apply {
                action = ACTION_START
            }
            startService(intent)
        }
    }

    override fun onRevoke() {
        super.onRevoke()
        // إعادة التشغيل عند سحب الأذونات
        val intent = Intent(this, MyVpnService::class.java).apply {
            action = ACTION_START
        }
        startService(intent)
    }
}

//class MyVpnService : VpnService() {
//
//    private var vpnInterface: ParcelFileDescriptor? = null
//    private lateinit var notification: Notification
//
//    companion object {
//        var isRunning = false
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = android.app.NotificationChannel(
//                "vpn_channel",
//                "VPN Service",
//                android.app.NotificationManager.IMPORTANCE_LOW
//            )
//            val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
//            manager.createNotificationChannel(channel)
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notification = Notification.Builder(this, "vpn_channel")
//                .setContentTitle("VPN يعمل الآن")
//                .setContentText("الحماية مفعّلة")
//                .setSmallIcon(android.R.drawable.ic_lock_lock)
//                .build()
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                startForeground(
//                    1,
//                    notification,
//                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
//                )
//            }
//        } else {
//            startForeground(1, notification)
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startVPN()
//        return START_STICKY
//    }
//
//    private fun startVPN() {
//        val builder = Builder()
//        builder
//            .addAddress("10.0.0.2", 32)
//            .addDnsServer("208.67.222.123")
//            .addDnsServer("208.67.220.123")
//            .setSession("SafeDNS")
//            .setBlocking(true)
//
//        vpnInterface?.close()
//        vpnInterface = builder.establish()
//        isRunning = true
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        vpnInterface?.close()
//        vpnInterface = null
//        isRunning = false
//        stopForeground(true)
//        stopSelf()
//    }
//}

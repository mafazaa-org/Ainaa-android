package com.mafazaa.ainaa.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.mafazaa.ainaa.R
import com.mafazaa.ainaa.core.Constants
import com.mafazaa.ainaa.domain.model.ProtectionLevel


class VpnKeepAliveService : Service() {

    private val vpnReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = intent?.getStringExtra("status")
            if (status == "DISCONNECTED") {
                restartVpn()
            }
        }
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "vpn_channel",
                "VPN Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()

        val notification = NotificationCompat.Builder(this, "vpn_channel")
            .setContentTitle("VPN Active")
            .setContentText("Maintaining VPN connection")
            .setSmallIcon(R.drawable.ic_auto_protect)
            .setOngoing(true)
            .build()
        createNotificationChannel()
        startForeground(1, notification)

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
           registerReceiver(vpnReceiver, IntentFilter("VPN_STATUS"), RECEIVER_NOT_EXPORTED)
       } else {
           registerReceiver(vpnReceiver, IntentFilter("VPN_STATUS"))
       }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkVpnAndReconnect()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(vpnReceiver) // لازم تشيل التسجيل
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartService = Intent(applicationContext, VpnKeepAliveService::class.java)
        restartService.setPackage(packageName)

        val pendingIntent = PendingIntent.getService(
            applicationContext,
            1,
            restartService,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            pendingIntent
        )

        super.onTaskRemoved(rootIntent)
    }



    private fun checkVpnAndReconnect() {
        if (!MyVpnService.Companion.isRunning) {
            restartVpn()
        }
    }

    private fun restartVpn() {
        val prefs = getSharedPreferences(Constants.VPN_SH_PREF_NAME, Context.MODE_PRIVATE)
        val savedLevelOrdinal = prefs.getInt(Constants.VPN_SH_PREF_KEY, ProtectionLevel.LOW.ordinal)

        val vpnIntent = Intent(this, MyVpnService::class.java).apply {
            action = MyVpnService.ACTION_START
            putExtra(MyVpnService.EXTRA_LEVEL, savedLevelOrdinal)
        }
        startService(vpnIntent)
    }

}
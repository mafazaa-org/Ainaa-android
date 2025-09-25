package com.mafazaa.ainaa.service

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import com.mafazaa.ainaa.AppActivity
import com.mafazaa.ainaa.Constants
import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.data.local.LocalData
import com.mafazaa.ainaa.model.DnsProtectionLevel
import org.koin.java.KoinJavaComponent.inject

class MyVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null
    private val localData: LocalData by inject(LocalData::class.java)

    companion object {
        private const val TAG = "MyVpnService"
        const val ACTION_START = "START_VPN"
        const val ACTION_STOP = "STOP_VPN"
        var isRunning = false//todo remove
    }

    override fun onCreate() {
        super.onCreate()
        MyNotificationManager.startForegroundService(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopVpn()
                return START_NOT_STICKY
            }

            else -> {
                val level = localData.dnsProtectionLevel
                startVpn(level)
                return START_STICKY
            }
        }
    }


    private fun startVpn(dnsProtectionLevel: DnsProtectionLevel) {
        val emptyIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, AppActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = Builder().apply {
            addAddress(Constants.vpnAddress, 32)
            addDnsServer(dnsProtectionLevel.primaryDns)
            addDnsServer(dnsProtectionLevel.secondaryDns)
            setSession("SafeDNS")
            setBlocking(true)
            setConfigureIntent(emptyIntent) // منع إيقاف الخدمة من الإشعار
            setMtu(1500)
        }

        Lg.d(TAG, "Starting VPN service with protection level: $dnsProtectionLevel")

        vpnInterface?.close()
        vpnInterface = builder.establish()
        if (vpnInterface == null) {
            Lg.e(TAG, "Failed to establish VPN interface")
            stopSelf()
            return
        }
        isRunning = true
    }

    private fun stopVpn() {
        Lg.d(TAG, "Stopping VPN service")
        vpnInterface?.close()
        vpnInterface = null
        isRunning = false
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()//todo
        isRunning = false
        Lg.d(TAG, "VPN service destroyed")
        if (isRunning) {
            // إعادة التشغيل التلقائي
            Intent(this, MyVpnService::class.java).apply {
                action = ACTION_START
            }

        }
    }

    override fun onRevoke() {
        super.onRevoke()//todo
        isRunning = false
        Lg.d(TAG, "VPN revoked")
        Intent(this, MyVpnService::class.java).apply {
            action = ACTION_START
        }

    }
}
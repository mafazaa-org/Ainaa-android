package com.mafazaa.ainaa.services

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.content.ContextCompat
import com.mafazaa.ainaa.core.Constants
import com.mafazaa.ainaa.ui.main.MainActivity
import com.mafazaa.ainaa.domain.model.ProtectionLevel

class MyVpnService: VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    companion object {
        const val EXTRA_LEVEL: String= "EXTRA_LEVEL"
        private const val TAG="MyVpnService"
        const val ACTION_START = "START_VPN"
        const val ACTION_STOP = "STOP_VPN"
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        MyNotificationManager.createNotificationChannel(this)
        MyNotificationManager.startForegroundService(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val ordinal= intent.getIntExtra(EXTRA_LEVEL, ProtectionLevel.LOW.ordinal)
                val level = ProtectionLevel.entries.getOrNull(ordinal)!!
                startVpn(level)
                return START_STICKY
            }

//            ACTION_STOP -> {
//                stopVpn()
//                return START_NOT_STICKY
//            }

            else -> {
                throw IllegalArgumentException("Unknown action: ${intent?.action}")
            }
        }
    }

    private fun startVpn(protectionLevel: ProtectionLevel) {
        val emptyIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = Builder().apply {
            addAddress(Constants.Address, 32)
            addDnsServer(protectionLevel.primaryDns)
            addDnsServer(protectionLevel.secondaryDns)
            setSession("SafeDNS")
            setBlocking(true)
            setConfigureIntent(emptyIntent)
            setMtu(1500)
        }
        Log.d(TAG, "Starting VPN service with protection level: $protectionLevel")

        vpnInterface?.close()
        vpnInterface = builder.establish()
        isRunning = true
    }

    private fun stopVpn() {
        vpnInterface?.close()
        vpnInterface = null



        val intent = Intent("VPN_STATUS").apply {
            putExtra("status", "DISCONNECTED")
        }
        sendBroadcast(intent)

    }



    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "VPN service destroyed, restarting to keep notification")
        if (isRunning) {
            val intent = Intent(this, MyVpnService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_LEVEL, ProtectionLevel.LOW.ordinal)
            }
            ContextCompat.startForegroundService(this, intent)
        }
    }



    override fun onRevoke() {
        super.onRevoke()
        isRunning = false

        val intent = Intent("VPN_STATUS").apply {
            putExtra("status", "DISCONNECTED")
        }
        sendBroadcast(intent)
    }

}
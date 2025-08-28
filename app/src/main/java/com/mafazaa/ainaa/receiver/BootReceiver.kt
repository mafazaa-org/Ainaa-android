package com.mafazaa.ainaa.receiver

import android.content.*
import android.util.Log
import androidx.core.content.*
import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.service.*
import org.koin.java.KoinJavaComponent.inject

class BootReceiver: BroadcastReceiver() {
    val localData: LocalData by inject(LocalData::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        if (localData.phoneNum.isNotBlank()&& !MyVpnService.isRunning) {//has vpn permission
            Lg.i(TAG, "Starting vpn on boot")
            ContextCompat.startForegroundService(
                context,
                Intent(
                    context,
                    MyVpnService::class.java
                ).apply {
                    action = MyVpnService.ACTION_START
                }
            )
        }

        if (localData.apps.isNotEmpty()){
            Lg.i(TAG, "Starting daily notification worker on boot")
            ContextCompat.startForegroundService(
                context,
                Intent(
                    context,
                    MonitorService::class.java
                )
            )
        }

    }


    companion object {
        private const val TAG = "BootReceiver"
    }
}
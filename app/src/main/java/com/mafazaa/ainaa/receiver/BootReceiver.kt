package com.mafazaa.ainaa.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.data.repository_impl.data_source.local_data_source.LocalData
import com.mafazaa.ainaa.service.MonitorService
import com.mafazaa.ainaa.service.MyVpnService
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
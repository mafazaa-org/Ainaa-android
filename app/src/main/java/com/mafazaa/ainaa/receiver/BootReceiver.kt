package com.mafazaa.ainaa.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.data.FakeFileRepo
import com.mafazaa.ainaa.hasAccessibilityPermission
import com.mafazaa.ainaa.hasVpnPermission
import com.mafazaa.ainaa.service.MyAccessibilityService.Companion.startAccessibilityService
import com.mafazaa.ainaa.startVpnService

class BootReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_REBOOT
        ) {
            return
        }
        Lg.fileRepo = FakeFileRepo
        Lg.i(TAG, "Device :${intent.action}")
        if (context.hasAccessibilityPermission()) {
            context.startAccessibilityService()
        }
        if (context.hasVpnPermission()) {
            Lg.i(TAG, "Starting vpn on boot")
            context.startVpnService()
        }

    }


    companion object {
        private const val TAG = "BootReceiver"
    }
}
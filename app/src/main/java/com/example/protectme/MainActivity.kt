package com.example.protectme

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.protectme.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit.MILLISECONDS

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var startTime = 0L

    private val updateUptime = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - startTime
            val uptime = String.format(
                "%02d:%02d:%02d",
                MILLISECONDS.toHours(millis),
                MILLISECONDS.toMinutes(millis) % 60,
                MILLISECONDS.toSeconds(millis) % 60)

            binding.txtUptime.text = uptime
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotificationUtils.createNotificationChannel(this)
        setupUI()
        checkVpnStatus()
    }

    private fun setupUI() {
        binding.btnToggleVpn.setOnClickListener {
            if (MyVpnService.isRunning) {
                Toast.makeText(this, R.string.cannot_disable_message, Toast.LENGTH_LONG).show()
            } else {
                prepareVpnService()
            }
        }

        binding.txtBlockedCount.text = SecurityTips.getBlockedSitesCount().toString()
        binding.txtSecurityTip.text = SecurityTips.getDailyTip()
    }

    private fun checkVpnStatus() {
        if (MyVpnService.isRunning) {
            startTime = System.currentTimeMillis() - 3600000 // مثال: ساعة مضت
            handler.post(updateUptime)
            updateProtectionUI(true)
        } else {
            updateProtectionUI(false)
        }
    }

    private fun prepareVpnService() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, VPN_REQUEST_CODE)
        } else {
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null)
        }
    }

    private fun updateProtectionUI(isActive: Boolean) {
        if (isActive) {
            binding.txtStatusTitle.text = getString(R.string.protection_active)
            binding.txtStatusSubtitle.text = getString(R.string.vpn_notification_content)
            binding.btnToggleVpn.text = getString(R.string.protection_active)

            // بدء حساب مدة التشغيل
            if (startTime == 0L) startTime = System.currentTimeMillis()
            handler.post(updateUptime)
        } else {
            binding.txtStatusTitle.text = getString(R.string.protection_inactive)
            binding.txtStatusSubtitle.text = getString(R.string.start_protection)
            binding.btnToggleVpn.text = getString(R.string.start_protection)
            handler.removeCallbacks(updateUptime)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            startVpnService()
        }
    }

    private fun startVpnService() {
        val intent = Intent(this, MyVpnService::class.java).apply {
            action = MyVpnService.ACTION_START
        }

        ContextCompat.startForegroundService(this, intent)
        MyVpnService.isRunning = true
        updateProtectionUI(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateUptime)
    }

    companion object {
        private const val VPN_REQUEST_CODE = 100
    }
}
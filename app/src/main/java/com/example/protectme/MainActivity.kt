package com.example.protectme

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.protectme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var startTime = 0L

    private val updateUptime = object : Runnable {
        override fun run() {
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeProtectGradientColor()

        NotificationUtils.createNotificationChannel(this)
        setupUI()
        checkVpnStatus()
    }

    private fun changeProtectGradientColor() {
        val shader = LinearGradient(
            0f, 0f, 0f, binding.protectYourDeviceTV.textSize,
            intArrayOf(this.getColor(R.color.green), this.getColor(R.color.black)),
            null,
            Shader.TileMode.CLAMP
        )
        binding.protectYourDeviceTV.paint.shader = shader
    }

    private fun setupUI() {
        binding.activeProtectionBtn.setOnClickListener {
            if (MyVpnService.isRunning) {
                Toast.makeText(this, "cannot_disable_message", Toast.LENGTH_LONG).show()
            } else {
                prepareVpnService()
            }
        }
    }

    private fun checkVpnStatus() {
        if (MyVpnService.isRunning) {
            startTime = System.currentTimeMillis() - 3600000 // مثال: ساعة مضت
            handler.post(updateUptime)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateUptime)
    }

    companion object {
        private const val VPN_REQUEST_CODE = 100
    }
}
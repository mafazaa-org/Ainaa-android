package com.mafazaa.ainaa

import android.content.*
import android.graphics.*
import android.net.*
import android.os.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.core.content.*
import com.mafazaa.ainaa.databinding.*
import com.mafazaa.ainaa.ui.*

class MainActivity: AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var startTime = 0L

    private val updateUptime = object: Runnable {
        override fun run() {
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //    binding = ActivityMainBinding.inflate(layoutInflater)
    //    setContentView(binding.root)

       // changeProtectGradientColor()

        NotificationUtils.createNotificationChannel(this)
  //      setupUI()
        checkVpnStatus()
    }

//    private fun changeProtectGradientColor() {
//        val shader = LinearGradient(
//            0f, 0f, 0f, binding.protectYourDeviceTV.textSize,
//            intArrayOf(this.getColor(R.color.green), this.getColor(R.color.black)),
//            null,
//            Shader.TileMode.CLAMP
//        )
//        binding.protectYourDeviceTV.paint.shader = shader
//    }

//    private fun setupUI() {
//        binding.activeProtectionBtn.setOnClickListener {
//            CustomConfirmDialog(
//                this,
//                dialogType = DialogType.enableProtection,
//                onConfirm = {
//                    if (MyVpnService.isRunning) {
//                        Toast.makeText(this, "already_running_message", Toast.LENGTH_LONG).show()
//                    } else {
//                        prepareVpnService()
//                    }
//                },
//                onCancel = {}
//            ).show()
//        }
//    }

    private fun checkVpnStatus() {
        if (MyVpnService.isRunning) {
            startTime = System.currentTimeMillis() - 3600000 // مثال: ساعة مضت
            handler.post(updateUptime)
            startActivity(Intent(this, SuccessActivity::class.java))
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
        startActivity(Intent(this, SuccessActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateUptime)
    }

    companion object {
        private const val VPN_REQUEST_CODE = 100
    }
}

package com.mafazaa.ainaa

import android.content.*
import android.os.*
import android.widget.*
import androidx.activity.*
import androidx.appcompat.app.*
import androidx.core.view.*
import com.mafazaa.ainaa.databinding.*
import com.mafazaa.ainaa.ui.*

class SuccessActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.supportBtn.setOnClickListener {
            val intent = Intent(this, SupportUsActivity::class.java)
            startActivity(intent)
        }
        binding.reportTv.setOnClickListener {
            val dialog = ReportProblemDialog()
            dialog.show(supportFragmentManager, "ReportProblemDialog")
        }
        binding.blockAppBtn.setOnClickListener {
//            val dialog = CustomConfirmDialog(
//                this,
//                DialogType.block,
//                onConfirm = {
//                    // todo
//                    Toast.makeText(this, "تم حجب التطبيق بنجاح", Toast.LENGTH_SHORT).show()
//                },
//                onCancel = {
//                    // Logic for cancel action if needed
//                }
//            )
//            dialog.show()
        }


    }
}
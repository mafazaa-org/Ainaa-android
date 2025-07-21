package com.mafazaa.ainaa

import android.content.*
import android.os.*
import androidx.activity.*
import androidx.appcompat.app.*
import androidx.core.net.*
import androidx.core.view.*
import com.mafazaa.ainaa.Constants.joinUrl
import com.mafazaa.ainaa.Constants.supportUrl
import com.mafazaa.ainaa.databinding.*

class SupportUsActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySupportUsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySupportUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.supportBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, supportUrl.toUri())
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            startActivity(intent)
        }
        binding.joinBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, joinUrl.toUri())
            startActivity(intent)
        }
    }
}

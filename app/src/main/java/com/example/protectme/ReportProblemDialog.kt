package com.example.protectme


import android.os.*
import android.view.*
import com.example.protectme.databinding.*
import com.google.android.material.bottomsheet.*

class ReportProblemDialog: BottomSheetDialogFragment() {

    private var binding: ReportProblemDialogBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ReportProblemDialogBinding.inflate(inflater, container, false)
        binding!!.closeButton.setOnClickListener {
            dismiss()
        }
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        // Optional: full width
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

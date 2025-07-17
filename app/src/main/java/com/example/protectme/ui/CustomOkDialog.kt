package com.example.protectme.ui

import android.app.*
import android.content.*
import android.view.*
import com.example.protectme.databinding.*

enum class DialogType(
    val title: String,
    val description: String,
    val positiveButtonText: String,
    val negativeButtonText: String,
) {
    block(
        "هل انت متأكد من من انك تريد حجب Youtube",
        "", "متأكد",
        "إلغاء"
    ),
    enableProtection(
        " هل ترغب في تفعيل الحماية على جهازك؟",
        "بمجرد الموافقة، سيتم تفعيل الحماية الفورية لضمان أمان جهازك أثناء التصفح.",
        "فعّل الحماية",
        "لاحقًا"
    ),
}

class CustomConfirmDialog(
    context: Context,
    private val dialogType: DialogType,
    private val onConfirm: () -> Unit,
    private val onCancel: () -> Unit = {},
): Dialog(context) {

    init {

        val binding = CustomOkDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        setCancelable(true)
        binding.apply {
            val params = root.layoutParams
            params.width = 1000
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            root.layoutParams = params
            tvTitle.text = dialogType.title
            tvDescription.text = dialogType.description
            btnConfirm.text = dialogType.positiveButtonText
            btnCancel.text = dialogType.negativeButtonText
            ivClose.setOnClickListener { dismiss() }


            tvTitle.text = dialogType.title
            tvDescription.text = dialogType.description
            btnConfirm.text = dialogType.positiveButtonText
            btnCancel.text = dialogType.negativeButtonText

            btnConfirm.setOnClickListener {
                onConfirm()
                dismiss()
            }

            btnCancel.setOnClickListener {
                onCancel()
                dismiss()
            }

            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }
}


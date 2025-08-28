package com.mafazaa.ainaa.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.mafazaa.ainaa.domain.model.UpdateState
import com.mafazaa.ainaa.ui.components.ReportLink
import com.mafazaa.ainaa.ui.components.TowColorText
import com.mafazaa.ainaa.ui.theme.*

@Composable
fun ProtectionActivatedScreen(
    onSupportClick: () -> Unit,
    onBlockAppClick: () -> Unit,
    onReportClick: () -> Unit,
    onConfirmProtectionClick: () -> Unit,
    onUpdateClick: (updateState: UpdateState) -> Unit = { /* Default no-op */ },
    updateState: UpdateState = UpdateState.NoUpdate,

    ) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "مبارك!! تم تفعيل الحماية",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

// Subtitle
        Text(
            text = "كيف أتأكد ان الحماية تعمل؟",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = red,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 24.dp)
                .clickable {
                    onConfirmProtectionClick()
                }
        )

        // Buttons row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            OutlinedButton(
                onClick = onSupportClick,
                border = BorderStroke(1.dp, red),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = red),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(text = "ادعمنا")
            }

            Button(
                onClick = onBlockAppClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(text = "حجب تطبيق معين")
            }
        }
        ReportLink(onReportClick = onReportClick)
        Spacer(modifier = Modifier.height(16.dp))
        val (black, red) = when (updateState) {
            UpdateState.NoUpdate -> Pair("لا يوجد تحديث متاح", "اضغط للتحقق")
            UpdateState.Checking -> Pair("جاري التحقق من وجود تحديثات", "")
            is UpdateState.Downloading -> Pair("جاري تحميل التحديث... الرجاء الانتظار", "")
            is UpdateState.Failed -> Pair("فشل تحميل التحديث", "حاول مرة أخرى")
            UpdateState.Downloaded -> Pair("تم تحميل التحديث", "تثبيت")
        }

        TowColorText(black = black, red = red, onClick = { onUpdateClick(updateState) })

    }
}


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
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.ui.theme.*

@Composable
fun ProtectionActivatedScreen(
    onSupportClick: () -> Unit,
    onBlockAppClick: () -> Unit,
    onReportClick: () -> Unit,
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
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        // Subtitle
        Text(
            text = "يمكنك أيضاً حجب تطبيق معين: اضغط الزر في الأسفل",
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
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


    }
}

@Preview
@Composable
fun ProtectionActivatedScreenPreview() {
    ProtectionActivatedScreen(
        onSupportClick = {},
        onBlockAppClick = {},
        onReportClick = {}
    )
}


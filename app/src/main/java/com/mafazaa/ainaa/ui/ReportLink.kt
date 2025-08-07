package com.mafazaa.ainaa.ui

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.ui.theme.*

@Composable
fun ReportLink(modifier: Modifier = Modifier, onReportClick: () -> Unit) {

    Text(
        buildAnnotatedString {
            append("اكتشفت ثغرة او موقع غير محجوب؟ ")
            withStyle(
                style = SpanStyle(
                    color = red,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("أخبرنا بها")
            }
        },
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.clickable(onClick = onReportClick)
    )
}
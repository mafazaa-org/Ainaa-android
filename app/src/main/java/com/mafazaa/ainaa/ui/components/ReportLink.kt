package com.mafazaa.ainaa.ui.components

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

@Composable
fun ReportLink(modifier: Modifier = Modifier, onReportClick: () -> Unit) {
    TowColorText(modifier, "اكتشفت ثغرة او موقع غير محجوب؟ ", "أخبرنا بها", onReportClick)
}

@Composable
fun TowColorText(modifier: Modifier= Modifier, black: String, red: String, onClick: () -> Unit) {
    Text(
        buildAnnotatedString {
            append("$black ")
            withStyle(
                style = SpanStyle(
                    color = com.mafazaa.ainaa.ui.theme.red,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(red)
            }
        },
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.clickable(onClick = onClick)
    )
}
package com.mafazaa.ainaa.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.mafazaa.ainaa.ui.theme.red

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
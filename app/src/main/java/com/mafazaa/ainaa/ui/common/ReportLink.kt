package com.mafazaa.ainaa.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReportLink(modifier: Modifier = Modifier, onReportClick: () -> Unit) {
    TwoColorText(modifier, "اكتشفت ثغرة او موقع غير محجوب؟ ", "أخبرنا بها", onReportClick)
}


package com.mafazaa.ainaa.ui.comp

import androidx.compose.runtime.*
import androidx.compose.ui.*

@Composable
fun ReportLink(modifier: Modifier = Modifier, onReportClick: () -> Unit) {
    TowColorText(modifier, "اكتشفت ثغرة او موقع غير محجوب؟ ", "أخبرنا بها", onReportClick)
}


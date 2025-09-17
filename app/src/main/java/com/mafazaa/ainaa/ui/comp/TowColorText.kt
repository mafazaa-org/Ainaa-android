package com.mafazaa.ainaa.ui.comp

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
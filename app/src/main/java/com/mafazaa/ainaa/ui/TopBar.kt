package com.mafazaa.ainaa.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.R
import com.mafazaa.ainaa.ui.theme.*

@Composable fun TopBar(
    blockSpecificApp: () -> Unit,
    supportUs: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 24.dp, end = 4.dp)
        ,
    ) {
        Text(
            text = "ادعمنا",
            color = red,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { supportUs() },
            style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline),
            lineHeight = 20.sp
        )

        Image(
            painter = painterResource(id = R.drawable.red), // Replace with actual drawable
            contentDescription = "Logo",
            modifier = Modifier
                .size(width = 100.dp, height = 40.dp)
                .align(Alignment.Center)
        )

        Text(
            text = "أدوات إضافية للأندرويد",
            color = red,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = (-2).dp)
                .clickable { blockSpecificApp() },
            style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline),
            lineHeight = 20.sp
        )
    }
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(blockSpecificApp = {}, supportUs = {})
}


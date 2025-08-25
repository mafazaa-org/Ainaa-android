package com.mafazaa.ainaa.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.R
import com.mafazaa.ainaa.ui.theme.*

@Composable fun TopBar(
    blockSpecificApp: () -> Unit,
    canBlock: Boolean,
    supportUs: () -> Unit,
    home: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 24.dp, end = 4.dp),
    ) {
        if (canBlock) {
            Text(
                text = "حجب تطبيق معين",
                color = red,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(y = (-2).dp)
                    .clickable { blockSpecificApp() },
                style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline),
                lineHeight = 17.sp
            )
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(y = (-2).dp)
            ) {}

        }

        Image(
            painter = painterResource(id = R.drawable.red), // Replace with actual drawable
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(.24f)
                .align(Alignment.Center)
                .clickable { home() }
        )
        Text(
            text = "ادعمنا",
            color = red,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { supportUs() },
            style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline),
            lineHeight = 20.sp
        )

    }
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(blockSpecificApp = {}, supportUs = {}, home = {}, canBlock = false)
}


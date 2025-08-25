package com.mafazaa.ainaa.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.R
import com.mafazaa.ainaa.ui.theme.*


@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    androidVersion: String,
    appVersion: Int,
    home: () -> Unit,
) {
    Box(
        modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Android:$androidVersion\nApp Version:$appVersion",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(start = 8.dp)
                ,
            color = red.copy(alpha = .7f),
            style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
            fontSize = 10.sp

        )
        Image(
            painter = painterResource(id = R.drawable.logo_red),
            contentDescription = "logo",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
                .clickable { home() }
                .fillMaxWidth(.29f),//todo
        )
    }


}

@Preview(showSystemUi = true)
@Composable
fun BottomBarPreview(modifier: Modifier = Modifier) {
    BottomBar(modifier, "14", 3) {}
}
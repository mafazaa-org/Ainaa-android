package com.mafazaa.ainaa.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.R

@Composable
fun BottomBar(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.logo_red),
            contentDescription = "logo", modifier = Modifier.align(Alignment.Center)//todo
        )
    }


}
@Preview
@Composable
fun BottomBarPreview(modifier: Modifier = Modifier) {
    Box(modifier = Modifier
        .width(400.dp)
        .height(400.dp)
        .background(Color.White)) {
        BottomBar()
    }
}
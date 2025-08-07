package com.mafazaa.ainaa.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.R


@Composable
fun BottomBar(modifier: Modifier = Modifier,home : ()->Unit ) {
    Column(
        modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_red),
            contentDescription = "logo", modifier = Modifier
                .padding(8.dp)
                .clickable{home()}
                .fillMaxWidth(.29f),//todo
        )
    }


}

@Preview(showSystemUi = true)
@Composable
fun BottomBarPreview(modifier: Modifier = Modifier) {
    BottomBar(modifier,{})
}
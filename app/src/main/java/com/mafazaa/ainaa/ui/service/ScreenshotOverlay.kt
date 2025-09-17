package com.mafazaa.ainaa.ui.service

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mafazaa.ainaa.R

@Composable
fun ScreenshotOverlay(
    modifier: Modifier = Modifier, onScreenShot: () -> Unit = {}, onClose: () -> Unit = {}
) {
    Row(
        modifier
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .5f),
                shape = MaterialTheme.shapes.large
            )
    ) {

        Icon(
            modifier = Modifier
                .padding(4.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.large
                )
                .clickable {
                    onScreenShot()
                },
            painter = painterResource(id = R.drawable.screenshot),
            contentDescription = "Screenshot"
        )


        Icon(
            modifier = Modifier
                .padding(4.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                )
                .clickable {
                    onClose()
                },
            painter = painterResource(id = R.drawable.baseline_close_24),
            contentDescription = "Close",

            )


    }
}
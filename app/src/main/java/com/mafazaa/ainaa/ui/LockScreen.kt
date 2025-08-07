package com.mafazaa.ainaa.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun LockScreen(onUnlock: () -> Unit) {
    androidx.compose.material3.Surface(
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(
                text = "App is locked",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            androidx.compose.material3.Button(onClick = onUnlock) {
                androidx.compose.material3.Text("Unlock")
            }
        }
    }
}
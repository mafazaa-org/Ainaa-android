package com.mafazaa.ainaa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.mafazaa.ainaa.R
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.ui.theme.*


@Composable
fun ConfirmDeleteDialog(
    app: AppInfo,
    onDismiss: () -> Unit,
    onConfirm: (AppInfo) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Close Icon
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "close",

                    )
                }

                // Title
                Text(
                    text = "هل انت متأكد من انك تريد مسح ${app.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 16.dp)
                )

                // Description
                Text(
                    text = "",//todo
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 24.dp)
                )

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = red)
                    ) {
                        Text("الغاء")
                    }

                    Button(
                        onClick = { onConfirm(app) },
                        colors = ButtonDefaults.buttonColors(containerColor = red)
                    ) {
                        Text("متأكد", color = Color.White)
                    }
                }
            }
        }
    }
}

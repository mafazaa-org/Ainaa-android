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
import com.mafazaa.ainaa.ui.theme.*


@Composable
fun EnableProtectionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
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
                    text = " هل ترغب في تفعيل الحماية على جهازك؟",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 16.dp)
                )

                // Description
                Text(
                    text = "بمجرد الموافقة، سيتم تفعيل الحماية الفورية لضمان أمان جهازك أثناء التصفح.",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
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
                        Text("لاحقاً")
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = red)
                    ) {
                        Text("فعل الحماية", color = Color.White)
                    }
                }
            }
        }
    }
}

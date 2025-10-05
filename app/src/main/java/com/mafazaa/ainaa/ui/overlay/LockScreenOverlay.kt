package com.mafazaa.ainaa.ui.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mafazaa.ainaa.domain.models.BlockReason
import com.mafazaa.ainaa.ui.common.TwoColorText

@Composable
fun LockScreenOverlay(
    reason: BlockReason,
    onShareLog: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "لقد تخطيت الحماية",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    when (reason) {
                        is BlockReason.TryingToDisable -> {
                            Text(
                                text = "لقد حاولت تعطيل الحماية، وهذا غير مسموح به.",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = reason.codeName
                            )
                        }

                        is BlockReason.UsingBlockedApp -> {
                            Text(
                                text = "لقد حاولت استخدام تطبيق محظور. ",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = reason.packageName
                            )
                        }
                    }
                    Text(
                        text = "إذا كنت تعتقد أن هذا خطأ، شارك ملف السجل معنا وتواصل معنا",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TwoColorText(
                        red = "مشاركة ملف السجل",
                        black = ""
                    ) { onShareLog() }
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Text(
                            text = "إغلاق",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


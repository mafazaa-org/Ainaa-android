package com.mafazaa.ainaa.ui.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.mafazaa.ainaa.R
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.toPainter
import com.mafazaa.ainaa.ui.theme.*


@Composable
fun BlockAppDialog(
    onDismiss: () -> Unit,
    appStates: List<AppInfo>,
    onBlockClick: (AppInfo) -> Unit,
) {

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(.9f)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Close icon and title row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                    Text(
                        text = "حجب تطبيق معين",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                )

                Text(
                    text = "بمجرد حجب برنامج، لن تتمكن من التراجع أو إعادة تشغيله لاحقاً",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    LazyColumn {
                        items(appStates){app->
                            AppBlockItem(app) { onBlockClick(app) }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = DividerDefaults.Thickness,
                                color = DividerDefaults.color
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                }
            }
        }
    }

}
@Composable
fun AppBlockItem(
    app: AppInfo,
    onBlockClick: (AppInfo) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onBlockClick(app) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (app.isSelected) Color.Gray else red,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            enabled = !app.isSelected,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(40.dp)
        ) {
            Text(text = if (app.isSelected) "محجوب" else "حجب")
        }
        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = app.icon?.toPainter() ?: painterResource(id = R.drawable.android),
                contentDescription = app.name,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = app.name,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBlockAppDialog() {
    val sampleApps = listOf(
        AppInfo("واتساب", null, "com.whatsapp", false),
        AppInfo("فيسبوك", null, "com.facebook.katana", true),
        AppInfo("انستغرام", null, "com.instagram.android", false)
    )
    BlockAppDialog(
        onDismiss = {},
        appStates = sampleApps,
        onBlockClick = {}
    )
}
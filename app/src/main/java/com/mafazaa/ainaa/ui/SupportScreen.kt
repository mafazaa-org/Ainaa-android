package com.mafazaa.ainaa.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.ui.theme.*

@Composable
fun SupportScreen(
    modifier: Modifier = Modifier,
    onSupportClick: () -> Unit = {},
    onJoinClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "ادعم مسيرتنا وساهم في تطوير مستقبل أكثر أماناً للإنترنت",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Box 1: الدعم المادي
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🎉 الدعم المادي",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "مساهمتك المالية تمكننا من تطوير خدماتنا، تحسين الحماية، وتقديم مزايا جديدة للجميع. حتى المساهمة البسيطة تحدث فرقاً كبيراً!",
                    fontSize = 14.sp,
                    color = gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = onSupportClick,
                    colors = ButtonDefaults.buttonColors(containerColor = red)
                ) {
                    Text("ادعمنا", color = Color.White)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "👨‍💻 انضم إلى فريق العمل",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "نبحث دائماً عن أشخاص يشاركونا الشغف لتقديم تجربة أفضل للمستخدمين.",
                    fontSize = 14.sp,
                    color = gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = onJoinClick,
                    colors = ButtonDefaults.buttonColors(containerColor = red)
                ) {
                    Text("انضمام", color = Color.White)
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SupportScreenPreview() {
    SupportScreen()
}

package com.mafazaa.ainaa.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mafazaa.ainaa.BuildConfig
import com.mafazaa.ainaa.ui.theme.red

@Composable
fun SupportScreen(
    modifier: Modifier = Modifier,
    onSupportClick: () -> Unit = {},
    onJoinClick: () -> Unit = {},
    onShareLogFile: () -> Unit = {},
    onStopBlocking: () -> Unit = {}
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
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Box 1: الدعم المادي
        Card(
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
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "مساهمتك المالية تمكننا من تطوير خدماتنا، تحسين الحماية، وتقديم مزايا جديدة للجميع. حتى المساهمة البسيطة تحدث فرقاً كبيراً!",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = onSupportClick,
                    colors = ButtonDefaults.buttonColors(containerColor = red)
                ) {
                    Text("ادعمنا")
                }
            }
        }


        Card(
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
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "نبحث دائماً عن أشخاص يشاركونا الشغف لتقديم تجربة أفضل للمستخدمين.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = onJoinClick,
                    colors = ButtonDefaults.buttonColors(containerColor = red)
                ) {
                    Text("انضمام")
                }
            }
        }
        TowColorText(black = "مشاركة ملف السجل", red = "اضغط هنا") {
            onShareLogFile()
        }
        if (BuildConfig.DEBUG) {
            Spacer(modifier = Modifier.height(16.dp))
            TowColorText(black = "إيقاف الحجب مؤقتاً", red = "اضغط هنا") {
                onStopBlocking()
            }
        }

    }
}
@Preview(showBackground = true)
@Composable
fun SupportScreenPreview() {
    SupportScreen()
}

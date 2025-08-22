package com.mafazaa.ainaa.ui

import android.net.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import coil3.compose.*
import com.mafazaa.ainaa.ui.theme.*

@Composable
fun HowItWorksDialog(
    onContactClicked: () -> Unit,
    onSafeSearchClicked: () -> Unit,
    image: Uri,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Scaffold(
modifier = Modifier.padding(bottom = 16.dp).clip(RoundedCornerShape(16.dp)),
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) {
                        Text("حسناً")
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(.9f).padding(innerPadding),
            ) {


                val title = " مبروك تفعيل الحماية!!"

                val introText = """
        إن وجدت الحماية غير مفعلة، يمكنك التواصل مع أحد ممثلي خدمة العملاء
        للتأكد من أن الحماية تم تفعيلها عن طريق البحث الأمن الخاص بجوجل،
    """.trimIndent()

                val failureText = """
        
        ماذا أفعل إن فشل تفعيل الحماية؟
        1. أغلق المتصفح وافتحه مرة أخرى
        2. أعد تشغيل الهاتف وافتحه مرة أخرى
    """.trimIndent()

                val outroText = """
        
        إن لم تستطع بعد تفعيل الحماية، يمكنك التواصل مع أحد ممثلي خدمة العملاء عن طريق الرابط التالي
    """.trimIndent()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    // Normal text
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        text = introText,

                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start
                    )

                    // First clickable link
                    Text(
                        text = "اضغط على الرابط",
                        color = red,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            // open browser
                            onSafeSearchClicked()
                        }
                    )
                    Image(
                        painter = rememberAsyncImagePainter(model = image),
                        contentDescription = "Safe Search Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, lightGray, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Text(
                        text = failureText,
                        textAlign = TextAlign.Start
                    )

                    // Second clickable link
                    Text(
                        text = "اضغط هنا للتواصل مع أحد ممثلي خدمة العملاء",
                        color = red,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            onContactClicked()
                        }
                    )

                    Text(text = outroText)




                }
            }
        }
    }
}
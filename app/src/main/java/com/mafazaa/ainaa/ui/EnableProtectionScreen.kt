package com.mafazaa.ainaa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.*

@Composable
fun EnableProtectionScreen(
    modifier: Modifier = Modifier,
    report: () -> Unit,
    enableProtection: (String) -> Unit,
    selectedLevel: ProtectionLevel,
    onLevelSelected: (ProtectionLevel) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // optional spacing between the two
        ) {
            ProtectionLevelSelector(selectedLevel, onLevelSelected)
            ProtectYourDevice(enableProtection, report)
        }
    }
}

@Composable
fun ProtectYourDevice(enableProtection: (String) -> Unit, report: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title and Subtitle Section
        Column(
            modifier = Modifier.padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "قم بحماية جهازك الآن",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "اضغط على الزر بالأسفل لتفعيل الحماية الفورية لجهازك",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        var phoneNumber by remember { mutableStateOf("") }
        // Phone Number Input Field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("رقم الهاتف") },
            placeholder = { Text("برجاء إدخال رقم الهاتف") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = Color.Gray,
//                unfocusedBorderColor = Color.Gray,
//                textColor = Color.White,
//                placeholderColor = Color.Gray
//            )
        )
        Spacer(Modifier.size(16.dp))

        // Enable Protection Button
        Button(
            onClick = { enableProtection(phoneNumber) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_white), // Replace with your lock icon
                    contentDescription = "Lock Icon",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تفعيل الحماية",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        // Link at the Bottom
        ReportLink(onReportClick = report)
    }
}

// Preview for Design Time
@Preview(showBackground = true, locale = "ar")
@Composable
fun PreviewEnableProtectionScreen() {
    EnableProtectionScreen(Modifier, {}, {}, ProtectionLevel.HIGH, {})
}
package com.mafazaa.ainaa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.R
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.ui.theme.*

@Composable
fun EnableProtectionScreen(
    modifier: Modifier = Modifier,
    report: () -> Unit,
    enableProtection: (ProtectionLevel, String) -> Unit,
    selectedLevel: ProtectionLevel,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp) // optional spacing between the two
        ) {
            var selectedLevel by remember { mutableStateOf(selectedLevel) }
            ProtectionLevelSelector(selectedLevel, {
                selectedLevel = it
            })
            ProtectYourDevice({ enableProtection(selectedLevel, it) }, report)
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
            modifier = Modifier.padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "قم بحماية جهازك الآن",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        var phoneNumber by remember { mutableStateOf("") }
        val isPhoneNumberValid by remember {
            derivedStateOf {
                phoneNumber.isNotEmpty() && phoneNumber.all { it.isDigit() }//todo
            }
        }
        // Phone Number Input Field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("رقم الهاتف") },
            placeholder = { Text("برجاء إدخال رقم الهاتف") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .wrapContentHeight(),
            isError = !isPhoneNumberValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

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
                disabledContainerColor = Color.LightGray,
                containerColor = red

            ),
            enabled = isPhoneNumberValid
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_white), // Replace with your lock icon
                    contentDescription = "Lock Icon",
                    tint = MaterialTheme.colorScheme.surface
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
    EnableProtectionScreen(Modifier, {}, {_,_->}, ProtectionLevel.HIGH, )
}
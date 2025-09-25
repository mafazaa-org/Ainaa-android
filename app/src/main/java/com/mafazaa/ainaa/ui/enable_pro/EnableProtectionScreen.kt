package com.mafazaa.ainaa.ui.enable_pro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mafazaa.ainaa.R
import com.mafazaa.ainaa.model.DnsProtectionLevel
import com.mafazaa.ainaa.ui.comp.ReportLink
import com.mafazaa.ainaa.ui.theme.red

@Composable
fun EnableProtectionScreen(
    modifier: Modifier = Modifier,
    report: () -> Unit,
    enableProtection: (DnsProtectionLevel, String) -> Unit,
    selectedLevel: DnsProtectionLevel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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
    EnableProtectionScreen(Modifier, {}, { _, _ -> }, DnsProtectionLevel.HIGH)
}
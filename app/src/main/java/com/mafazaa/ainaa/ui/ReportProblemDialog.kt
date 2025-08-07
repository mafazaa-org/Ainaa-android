package com.mafazaa.ainaa.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.ui.theme.*

@Composable
fun ReportProblemDialog(
    onClose: () -> Unit,
    onSubmit: (Report) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp)
            ) {
                // Header with close button and title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                    Text(
                        text = "الإبلاغ عن مشكلة",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Name Field
                Text("الاسم", fontSize = 14.sp, color = Color.Black)
                OutlinedTextField(
                    value = name,
                    onValueChange = { it: String -> name = it },
                    placeholder = {
                        Text(
                            "برجاء إدخال الاسم",
                            color = lightGray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                )

                // Phone Field
                Spacer(Modifier.height(12.dp))
                Text("رقم الهاتف", fontSize = 14.sp, color = Color.Black)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { it: String -> phone = it },
                    placeholder = { Text("رقم الهاتف", color = lightGray, fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                )

                // Email Field
                Spacer(Modifier.height(12.dp))
                Text("البريد الإلكتروني", fontSize = 14.sp, color = Color.Black)
                OutlinedTextField(
                    value = email,
                    onValueChange = { it: String -> email = it },
                    placeholder = {
                        Text(
                            "برجاء إدخال البريد الإلكتروني",
                            color = lightGray,
                            fontSize = 12.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                )

                // Problem Field
                Spacer(Modifier.height(12.dp))
                Text("المشكلة", fontSize = 14.sp, color = Color.Black)
                OutlinedTextField(
                    value = problem,
                    onValueChange = { it: String -> problem = it },
                    placeholder = { Text("اكتب هنا..", color = lightGray, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    textStyle = LocalTextStyle.current.copy(
                        textDirection = TextDirection.Rtl,
                        textAlign = TextAlign.Right
                    ),
                    maxLines = 5
                )

                // Submit Button
                Button(
                    onClick = { onSubmit(Report(name, phone, email, problem)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)

                ) {
                    Text("إرسال", color = Color.White)
                }

                // Footer Message
                Text(
                    text = "سنتواصل معك عبر البريد الإلكتروني أو الهاتف في أقرب وقت.",
                    fontSize = 12.sp,
                    color = lightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        }
    }
}

@Preview(locale = "ar")
@Composable
fun ReportProblemDialogPreview() {
    ReportProblemDialog(
        onClose = {},
        onSubmit = { _ -> }
    )
}

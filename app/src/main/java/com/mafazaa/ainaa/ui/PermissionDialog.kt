package com.mafazaa.ainaa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.mafazaa.ainaa.PermissionState

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    permissionState: PermissionState,
) {
val (permissionTitle, permissionDescription) = when (permissionState) {
    PermissionState.UsageStats -> "إذن إحصائيات الاستخدام" to "يحتاج هذا التطبيق إلى إذن إحصائيات الاستخدام لمراقبة التطبيقات التي تعمل."
    PermissionState.Overlay -> "إذن العرض فوق التطبيقات" to "يحتاج هذا التطبيق إلى إذن العرض فوق التطبيقات للظهور فوق التطبيقات الأخرى."
    PermissionState.Vpn -> "إذن VPN" to "يحتاج هذا التطبيق إلى إذن VPN لإنشاء اتصال آمن."
    PermissionState.Granted -> "" to "" // لا يجب أن يحدث ذلك
}

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(.9f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = permissionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = permissionDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onClick) {
                        Text("موافق")
                    }
                }
            }
        }
    }
}


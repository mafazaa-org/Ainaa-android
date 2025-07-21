package com.mafazaa.ainaa.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mafazaa.ainaa.ui.theme.*

enum class ProtectionLevel {
    HIGH, LOW
}

@Composable
fun ProtectionLevelSelector(
    selectedLevel: ProtectionLevel,
    onLevelSelected: (ProtectionLevel) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start) {

        Text(
            text = "اختر مستوى الحماية",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Right
        )

        // High Protection Card
        ProtectionCard(
            title = "الحماية العالية",
            description = "توفر لك حماية شاملة من معظم أنواع المحتوى غير المرغوب فيه، مما يضمن تجربة تصفح أكثر أماناً وراحة مثل:",
            examples = listOf("كل ما في الحماية المنخفضة", "الموسيقى", "الأفلام", "التيك توك"),
            selected = selectedLevel == ProtectionLevel.HIGH,
            onClick = { onLevelSelected(ProtectionLevel.HIGH) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Low Protection Card
        ProtectionCard(
            title = "الحماية المنخفضة",
            description = "تحجب لك فقط الأساسيات التي قد تُزعجك أو تُعدّ غير مناسبة. مع إبقاء معظم المحتوى متاحاً لتصفح أكثر حرية مثل:",
            examples = listOf("الإباحية", "القمار", "الإعلانات", "المزاح", "الأدب غير المناسب", "المواقع العربية الغير لائقة"),
            selected = selectedLevel == ProtectionLevel.LOW,
            onClick = { onLevelSelected(ProtectionLevel.LOW) }
        )
    }
}

@Composable
fun ProtectionCard(
    title: String,
    description: String,
    examples: List<String>,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) red else Color.LightGray

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp),
        color = Color.Black,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = selected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = red
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                textAlign = TextAlign.Right
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                examples.forEach { label ->
                    Text(
                        text = label,
                        color = red,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .background(Color(0xFFFFEBEE), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
@Preview(locale = "ar")
@Composable
fun ProtectionLevelSelectorPreview() {
    var selectedLevel by remember { mutableStateOf(ProtectionLevel.HIGH) }
    ProtectionLevelSelector(
        selectedLevel = selectedLevel,
        onLevelSelected = { selectedLevel = it }
    )
}

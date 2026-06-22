package com.example.AdminPanel.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun FilterDropdown(label: String, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    OutlinedCard(
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 13.sp, color = Color.Gray)
            Icon(icon ?: Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

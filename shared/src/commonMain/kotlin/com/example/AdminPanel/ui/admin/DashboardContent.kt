package com.example.AdminPanel.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AdminPanel.ui.components.BodyText
import com.example.AdminPanel.ui.components.HeaderText
import com.example.AdminPanel.ui.components.SubHeaderText

@Composable
fun DashboardContent(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column {
        SubHeaderText(uiState.welcomeMessage)
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            uiState.stats.forEach { stat ->
                StatCard(
                    title = stat.title,
                    value = stat.value,
                    change = stat.change,
                    icon = stat.icon,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            // Placeholder for Chart
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                HeaderText("Users Overview", modifier = Modifier.padding(bottom = 16.dp))
                BodyText("Mock Chart Visualization", modifier = Modifier.align(Alignment.Center))
            }
            
            // Placeholder for Distribution
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                HeaderText("Status", modifier = Modifier.padding(bottom = 16.dp))
                BodyText("Distribution View", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, change: String, icon: ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    BodyText(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = change,
                style = MaterialTheme.typography.labelMedium,
                color = if (change.startsWith("+")) Color(0xFF2E7D32) else if (change.startsWith("-")) Color(0xFFC62828) else Color.Gray
            )
        }
    }
}

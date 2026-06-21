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
import com.example.AdminPanel.ui.components.StatCard
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


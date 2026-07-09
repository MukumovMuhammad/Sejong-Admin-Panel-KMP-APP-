package com.example.AdminPanel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.animation.core.*
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush


@Composable
fun StatCard(
    title: String,
    value: String,
    change: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
    isLoading: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Box Container
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (isLoading) Color.LightGray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primaryContainer,
                            CircleShape
                        )
                        .shimmerLoadingAnimation(isLoading), // Shimmer effect applied
                    contentAlignment = Alignment.Center
                ) {
                    if (!isLoading) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    if (isLoading) {
                        // Title Placeholder Strip
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(16.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .shimmerLoadingAnimation(true)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Value Placeholder Strip
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(22.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .shimmerLoadingAnimation(true)
                        )
                    } else {
                        BodyText(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                // Bottom Change Progress Text Line Placeholder Strip
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .shimmerLoadingAnimation(true)
                )
            } else {
                Text(
                    text = change,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (change.startsWith("+")) Color(0xFF2E7D32) else if (isDanger) Color.Red else Color.Gray
                )
            }
        }
    }
}





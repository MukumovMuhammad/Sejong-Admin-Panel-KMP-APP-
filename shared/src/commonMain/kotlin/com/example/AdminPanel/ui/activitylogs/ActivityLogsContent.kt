package com.example.AdminPanel.ui.activitylogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AdminPanel.data.model.AuditLog
import com.example.AdminPanel.data.model.VisualAuditLog
import com.example.AdminPanel.ui.components.*
import com.example.AdminPanel.ui.theme.*
import com.example.AdminPanel.data.utills.getFormattedTimeOfPost

@Composable
fun ActivityLogsContent(viewModel: ActivityLogsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Dashboard > Activity Logs", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                HeaderText("System Activity Audit")
                Spacer(modifier = Modifier.height(4.dp))
                Text("Track all administrative actions across the system. (Total: ${uiState.total})", color = Color.Gray, fontSize = 14.sp)
            }

            IconButton(onClick = { viewModel.loadLogs() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Filters Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterDropdown(
                    label = "Action",
                    options = listOf("All Actions", "create", "update", "delete"),
                    selectedOption = uiState.selectedAction ?: "All Actions",
                    onOptionSelected = {  },
                    modifier = Modifier.weight(1f)
                )

                FilterDropdown(
                    label = "Model",
                    options = listOf("All Models", "User", "Book", "Group", "Announcement"),
                    selectedOption = uiState.selectedModel ?: "All Models",
                    onOptionSelected = { },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = uiState.selectedAdmin ?: "",
                    onValueChange = { },
                    placeholder = { Text("Filter by Admin...") },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Table Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Admin", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Action", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Resource", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Changes", modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Timestamp", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // List
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                if (uiState.isLoading && uiState.logs.isEmpty()) {
                    items(10) {
                        LoadingLogPlaceholder()
                    }
                } else if (uiState.logs.isEmpty()) {
                    item {
                        EmptyStateComponent(
                            title = "No activity logs found",
                            icon = Icons.Default.Info,
                            modifier = Modifier.fillMaxWidth().padding(top = 100.dp)
                        )
                    }
                } else {
                    items(uiState.logs) { log ->
                        LogEntryRow(log)
                    }

                    if (uiState.hasMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(onClick = { }) {
                                    Text("Load More Activities")
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading && uiState.logs.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun LogEntryRow(visualLog: VisualAuditLog) {
    val log = visualLog.rawLog
    val (date, time) = log.timestamp.getFormattedTimeOfPost()
    val modifications = visualLog.changesDisplay
    val inlineChangesText = remember(log.id) {
        if (modifications.isNotEmpty()) {
            modifications.joinToString(", ") { "${it.fieldName}: ${it.description}" }
        } else {
            "No changes recorded"
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Admin
            Text(log.admin_user, modifier = Modifier.weight(1.5f), fontSize = 13.sp, fontWeight = FontWeight.Medium)

            // Action Badge
            Box(modifier = Modifier.weight(1f)) {
                ActionBadge(log.action)
            }

            // Resource
            Column(modifier = Modifier.weight(1.5f)) {
                Text(log.model_name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("ID: ${log.object_id ?: "N/A"}", fontSize = 11.sp, color = Color.Gray)
            }

            // Changes
            Box(modifier = Modifier.weight(2.5f)) {
                Text(
                    text = inlineChangesText,
                    fontSize = 12.sp,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Timestamp
            Column(modifier = Modifier.weight(1.5f)) {
                Text(date, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text(time, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ActionBadge(action: String) {
    val (bgColor, textColor) = when (action.lowercase()) {
        "create" -> Success.copy(alpha = 0.1f) to Success
        "update" -> BrandBlue.copy(alpha = 0.1f) to BrandBlue
        "delete" -> BrandRed.copy(alpha = 0.1f) to BrandRed
        else -> Color.LightGray.copy(alpha = 0.2f) to Color.DarkGray
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = action.uppercase(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun LoadingLogPlaceholder() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .padding(horizontal = 8.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .shimmerLoadingAnimation(true)
                )
            }
        }
    }
}

package com.example.AdminPanel.ui.groups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.AdminPanel.data.model.Group
import com.example.AdminPanel.ui.components.*

@Composable
fun GroupsContent(viewModel: GroupsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var panelWidth by remember { mutableStateOf(450.dp) }
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Dashboard > Groups", color = Color.Gray, fontSize = 12.sp)
                    HeaderText("Groups")
                    Text("Create, manage and organize all groups and classes.", color = Color.Gray, fontSize = 14.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecondaryButton(text = "Export", onClick = {}, modifier = Modifier.width(120.dp), icon = Icons.Default.Share)
                    PrimaryButton(text = "Add Group", onClick = { showAddDialog = true }, modifier = Modifier.width(150.dp), icon = Icons.Default.Add)
                }
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard("Total Groups", uiState.totalGroups.toString(), "All active groups", Icons.Default.Person, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Active Groups", uiState.activeGroups.toString(), "Currently running", Icons.Default.CheckCircle, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Upcoming Groups", uiState.upcomingGroups.toString(), "Starting soon", Icons.Default.DateRange, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Total Students", uiState.totalStudents.toString(), "Across all groups", Icons.Default.AccountBox, Modifier.weight(1f), isLoading = uiState.isLoading)
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = { Text("Search groups...") },
                            modifier = Modifier.width(300.dp),
                            shape = RoundedCornerShape(8.dp),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true
                        )
                        
                        OutlinedButton(onClick = {}, shape = RoundedCornerShape(8.dp)) {
                            Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Filter")
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("View:", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledIconButton(
                            onClick = {},
                            shape = RoundedCornerShape(8.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) { Icon(Icons.Default.List, contentDescription = null) }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = {}) { Icon(Icons.Default.Menu, contentDescription = null) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Table Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Group Name", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Level", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Teacher", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Students", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Schedule", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Status", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Actions", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }

            // List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (uiState.isLoading && uiState.groups.isEmpty()) {
                    items(6) {
                        GroupRowPlaceholder()
                    }
                } else {
                    items(uiState.filteredGroups) { group ->
                        GroupRow(
                            group = group,
                            isSelected = uiState.selectedGroup?.id == group.id,
                            onClick = { viewModel.selectGroup(group) },
                            onDelete = { viewModel.deleteGroup(group.id ?: "") }
                        )
                    }
                }
            }
        }

        // Add Group Dialog
        if (showAddDialog) {
            AddGroupDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name ->
                    viewModel.createGroup(name)
                    showAddDialog = false
                }
            )
        }

        // Side Panel
        AnimatedVisibility(
            visible = uiState.selectedGroup != null,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Row(modifier = Modifier.width(panelWidth).fillMaxHeight()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .background(Color.Transparent)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                panelWidth = (panelWidth - dragAmount.x.toDp()).coerceIn(400.dp, 800.dp)
                            }
                        }
                )
                
                if (uiState.selectedGroup != null){
                    GroupDetailsPanel(
                        group = uiState.selectedGroup!!,
                        onClose = { viewModel.selectGroup(null) },
                        onDelete = { viewModel.deleteGroup(uiState.selectedGroup?.id ?: "") }
                    )
                }

            }
        }
    }
}

@Composable
fun GroupRow(group: Group, isSelected: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Group Name & Code
            Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = group.name.take(4).uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(group.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Level 1", fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Level
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        group.level ?: "Beginner",
                        color = Color(0xFF2E7D32),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Teacher
            Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.LightGray)) {
                    AsyncImage(model = group.teacher_avatar, contentDescription = null, modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(group.teacher_name ?: "Assign Teacher", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(group.teacher_email ?: "email@example.com", fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Students
            Row(modifier = Modifier.weight(0.8f), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(group.students_count.toString(), fontSize = 13.sp)
            }

            // Schedule
            Column(modifier = Modifier.weight(1.5f)) {
                Text("Mon, Wed, Fri", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text("09:00 - 10:30", fontSize = 11.sp, color = Color.Gray)
            }

            // Status
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    color = if (group.status == "Active") Color(0xFFE8F5E9) else Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        group.status ?: "Active",
                        color = if (group.status == "Active") Color(0xFF2E7D32) else Color(0xFF1976D2),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Actions
            Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = {}) { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
                IconButton(onClick = onClick) { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Red.copy(alpha = 0.7f)) }
            }
        }
    }
}

@Composable
fun GroupRowPlaceholder() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).shimmerLoadingAnimation(true))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Box(modifier = Modifier.width(120.dp).height(16.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.width(60.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.width(80.dp).height(24.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.widthIn(max = 500.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(32.dp)) {
                HeaderText("Add New Group")
                Spacer(modifier = Modifier.height(24.dp))
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Group Name *",
                    placeholder = "Enter group name (e.g. CS-101)"
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SecondaryButton(text = "Cancel", onClick = onDismiss, modifier = Modifier.weight(1f))
                    PrimaryButton(text = "Create Group", onClick = { onConfirm(name) }, modifier = Modifier.weight(1f), enabled = name.isNotBlank())
                }
            }
        }
    }
}

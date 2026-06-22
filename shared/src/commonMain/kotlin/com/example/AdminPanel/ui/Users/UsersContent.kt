package com.example.AdminPanel.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.ui.components.*
import com.example.AdminPanel.ui.announcements.FilterDropdown

@Composable
fun UsersContent(viewModel: UsersViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        // Left Column: List and Stats
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = if (uiState.selectedUser != null) 16.dp else 0.dp)
        ) {
            // Header
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Text("Dashboard > Users", color = Color.Gray, fontSize = 12.sp)
                HeaderText("Users Management")
                Text("View, manage and organize all users in the system.", color = Color.Gray, fontSize = 14.sp)
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserStatCard("Total Users", uiState.totalCount.toString(), "+8.2%", Icons.Default.Person, Modifier.weight(1f))
                UserStatCard("Students", uiState.studentsCount.toString(), "+6.1%", Icons.Default.AccountBox, Modifier.weight(1f))
                UserStatCard("Teachers", uiState.teachersCount.toString(), "+2.4%", Icons.Default.Person, Modifier.weight(1f))
                UserStatCard("Admins", uiState.adminsCount.toString(), "", Icons.Default.Lock, Modifier.weight(1f))
                UserStatCard("Pending", uiState.pendingCount.toString(), "12.7%", Icons.Default.Refresh, Modifier.weight(1f), isWarning = true)
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
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Search users by name, username, email...") },
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )
                    
                    FilterDropdown("Status", Modifier.weight(0.8f))
                    FilterDropdown("Verification", Modifier.weight(0.8f))
                    FilterDropdown("Group", Modifier.weight(0.8f))
                    
                    TextButton(onClick = {}) { Text("Clear Filters") }
                    
                    SecondaryButton(text = "Import Students", onClick = {}, modifier = Modifier.width(160.dp), icon = Icons.Default.Share)
                    PrimaryButton(text = "Add User", onClick = {}, modifier = Modifier.width(140.dp), icon = Icons.Default.Add)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    Text("User", modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Username", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Status", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Verification", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Group", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Joined At", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Actions", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }

            // List
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(uiState.users) { user ->
                        UserRow(
                            user = user,
                            isSelected = uiState.selectedUser?.id == user.id,
                            onClick = { viewModel.selectUser(user) },
                            onDelete = { viewModel.deleteUser(user.id) }
                        )
                    }
                }
            }
        }

        // Right Column: User Details
        if (uiState.selectedUser != null) {
            UserDetailsPanel(
                user = uiState.selectedUser!!,
                onClose = { viewModel.selectUser(null) }
            )
        }
    }
}

@Composable
fun UserStatCard(title: String, value: String, change: String, icon: ImageVector, modifier: Modifier, isWarning: Boolean = false) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(if (isWarning) Color(0xFFFFF3E0) else Color(0xFFE3F2FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (isWarning) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontSize = 11.sp, color = Color.Gray)
                    if (change.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = if (isWarning) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                change, 
                                fontSize = 10.sp, 
                                color = if (isWarning) Color.Red else Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(if (isWarning) "Awaiting verification" else "All registered users", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun UserRow(user: User, isSelected: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 2.dp else 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Info
            Row(modifier = Modifier.weight(2.5f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray)
                ) {
                    AsyncImage(
                        model = user.avatar,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(user.fullname ?: "No Name", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(user.email ?: "", fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Username
            Text(user.username, modifier = Modifier.weight(1.5f), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)

            // Status
            Box(modifier = Modifier.weight(1f)) {
                StatusBadge(user.status)
            }

            // Verification
            Box(modifier = Modifier.weight(1.2f)) {
                VerificationBadge(user.verification_status)
            }

            // Group
            Text(user.group ?: "-", modifier = Modifier.weight(1f), fontSize = 13.sp)

            // Joined At
            Column(modifier = Modifier.weight(1.5f)) {
                Text("15 Jun 2026", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text("10:30 AM", fontSize = 11.sp, color = Color.Gray)
            }

            // Actions
            Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = onClick) { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = {}) { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Red.copy(alpha = 0.7f)) }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "Student" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "Teacher" -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        "Admin" -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
        else -> Color(0xFFF5F5F5) to Color(0xFF616161)
    }
    Surface(
        color = color.first,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            status, 
            color = color.second, 
            fontSize = 10.sp, 
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun VerificationBadge(status: String) {
    val (bgColor, textColor, icon) = when (status) {
        "Approved" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.CheckCircle)
        "Pending" -> Triple(Color(0xFFFFF3E0), Color(0xFFFF9800), Icons.Default.Refresh)
        "Rejected" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.Close)
        else -> Triple(Color(0xFFF5F5F5), Color(0xFF616161), Icons.Default.Info)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = textColor)
        Spacer(modifier = Modifier.width(4.dp))
        Text(status, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

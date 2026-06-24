package com.example.AdminPanel.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.data.utills.getFormattedTimeOfPost
import com.example.AdminPanel.ui.components.*

@Composable
fun UserDetailsPanel(user: User, onClose: () -> Unit, onDelete: () -> Unit) {

    val (formattedDate,formattedTime ) = user.date_joined.getFormattedTimeOfPost()

    Surface(
        modifier = Modifier
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                HeaderText("User Details", modifier = Modifier.padding(bottom = 0.dp))
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = null) }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                // Profile Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        AsyncImage(
                            model = user.avatar,
                            contentDescription = "Image Avatar",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(user.fullname ?: "No Name", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        VerificationBadge(user.verification_status)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(user.email ?: "No Email", fontSize = 14.sp, color = Color.Gray)
                        Text(user.phone_number ?: "No Phone", fontSize = 14.sp, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(user.status)
                            Surface(color = Color(0xFFF3E5F5), shape = RoundedCornerShape(4.dp)) {
                                Text(user.group ?: "No Group", color = Color(0xFF7B1FA2), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Text("ID: ${user.id.take(8)}...", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Tabs
                var selectedTab by remember { mutableStateOf(0) }
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Profile", modifier = Modifier.padding(vertical = 12.dp), fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Activity", modifier = Modifier.padding(vertical = 12.dp), fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                    }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Text("Achievements", modifier = Modifier.padding(vertical = 12.dp), fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Personal Information
                DetailSection("Personal Information") {
                    DetailRow("Full Name", user.fullname ?: "-")
                    DetailRow("Username", user.username)
                    DetailRow("Email", user.email ?: "-")
                    DetailRow("Phone Number", user.phone_number ?: "-")
                    DetailRow("Date of Birth", user.date_of_birth ?: "-")
                    DetailRow("Date Joined", "${formattedDate} at ${formattedTime} " ?: "-")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Academic Information
                DetailSection("Academic Information") {
                    DetailRow("Status", user.status)
                    DetailRow("Verification Status", user.verification_status)
                    DetailRow("Group", user.group ?: "-")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Additional Information
                DetailSection("Additional Information") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Avatar", color = Color.Gray, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.LightGray))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Avatar", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    DetailRow("Last Login", "15 Jun 2026, 09:20 AM")
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            // Footer Actions
            Surface(
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit User", fontSize = 13.sp)
                    }
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Change Status", fontSize = 13.sp)
                    }
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(0.8f).height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

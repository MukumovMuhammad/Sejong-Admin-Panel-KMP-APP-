package com.example.AdminPanel.ui.admin

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminPanelScreen(viewModel: AdminViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val sidebarWidth by animateDpAsState(if (uiState.isSidebarExpanded) 250.dp else 80.dp)

    Row(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FB))) {
        // Sidebar
        Sidebar(
            expanded = uiState.isSidebarExpanded,
            selectedTab = uiState.selectedTab,
            onTabSelected = viewModel::selectTab,
            onToggle = viewModel::toggleSidebar,
            width = sidebarWidth,
            username = uiState.username,
            role = uiState.role
        )

        // Main Content
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(selectedTab = uiState.selectedTab)
            Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                when (uiState.selectedTab) {
                    AdminTab.Dashboard -> DashboardContent()
                    else -> PlaceholderContent(uiState.selectedTab.name)
                }
            }
        }
    }
}

@Composable
fun Sidebar(
    expanded: Boolean,
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    onToggle: () -> Unit,
    width: androidx.compose.ui.unit.Dp,
    username: String,
    role: String
) {
    Column(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .background(Color(0xFF0D47A1))
            .padding(vertical = 24.dp)
    ) {
        // Logo / Brand
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (expanded) Arrangement.Start else Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("S", color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
            }
            if (expanded) {
                Spacer(modifier = Modifier.width(12.dp))
                Text("Sejong", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Navigation Items
        val navItems = listOf(
            AdminTab.Dashboard to Icons.Default.Home,
            AdminTab.Users to Icons.Default.List,
            AdminTab.PendingApprovals to Icons.Default.Person,
            AdminTab.Groups to Icons.Default.Person,
            AdminTab.Schedules to Icons.Default.Star,
            AdminTab.Announcements to Icons.Default.Notifications,
            AdminTab.Notifications to Icons.Default.Notifications,
            AdminTab.ELibrary to Icons.Default.DateRange,
            AdminTab.Settings to Icons.Default.Settings,
            AdminTab.ActivityLogs to Icons.Default.Search
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(navItems) { (tab, icon) ->
                SidebarItem(
                    tab = tab,
                    icon = icon,
                    isSelected = selectedTab == tab,
                    expanded = expanded,
                    onClick = { onTabSelected(tab) }
                )
            }
        }

        // Profile Info
        if (expanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(40.dp).background(Color.White, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(username, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(role, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .size(40.dp)
                    .background(Color.White, CircleShape)
            )
        }

        IconButton(
            onClick = onToggle,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                if (expanded) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun SidebarItem(
    tab: AdminTab,
    icon: ImageVector,
    isSelected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (expanded) Arrangement.Start else Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        if (expanded) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(tab.name, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun TopBar(selectedTab: AdminTab) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(selectedTab.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search anything...") },
                modifier = Modifier.width(300.dp),
                shape = CircleShape,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = {}) { Icon(Icons.Default.Notifications, contentDescription = null) }
            IconButton(onClick = {}) { Icon(Icons.Default.Star, contentDescription = null) }
        }
    }
}

@Composable
fun DashboardContent() {
    Column {
        Text("Welcome back, Admin!", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Total Users", "542", "+16 this week", Icons.Default.Person, Modifier.weight(1f))
            StatCard("Students", "426", "+8 this week", Icons.Default.Search, Modifier.weight(1f))
            StatCard("Teachers", "48", "+2 this week", Icons.Default.AccountBox, Modifier.weight(1f))
            StatCard("Pending Approvals", "9", "-3 this week", Icons.Default.Person, Modifier.weight(1f))
            StatCard("Groups", "12", "No change", Icons.Default.List, Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            // Placeholder for Chart
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Text("Users Overview (Mock Chart)", fontWeight = FontWeight.Bold)
            }
            
            // Placeholder for Distribution
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Text("User Status Distribution", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, change: String, icon: ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color(0xFFE3F2FD), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF0D47A1))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontSize = 12.sp, color = Color.Gray)
                    Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(change, fontSize = 10.sp, color = if (change.startsWith("+")) Color.Green else if (change.startsWith("-")) Color.Red else Color.Gray)
        }
    }
}

@Composable
fun PlaceholderContent(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for $title Coming Soon...", fontSize = 20.sp, color = Color.Gray)
    }
}

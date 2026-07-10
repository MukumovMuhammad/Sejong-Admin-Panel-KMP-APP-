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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AdminPanel.ui.profile.SettingsScreen
import com.example.AdminPanel.ui.components.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.AdminPanel.ui.ELibrary.ELibraryContent
import com.example.AdminPanel.ui.ELibrary.ELibraryViewModel
import com.example.AdminPanel.ui.activitylogs.ActivityLogsContent
import com.example.AdminPanel.ui.activitylogs.ActivityLogsViewModel
import com.example.AdminPanel.ui.announcements.AnnouncementsContent
import com.example.AdminPanel.ui.announcements.AnnouncementsViewModel
import com.example.AdminPanel.ui.users.UsersContent
import com.example.AdminPanel.ui.users.UsersViewModel
import com.example.AdminPanel.ui.groups.GroupsContent
import com.example.AdminPanel.ui.groups.GroupsViewModel

@Composable
fun AdminPanelScreen(viewModel: AdminViewModel, onLogOut: () -> Unit, isMobile: Boolean = false) {
    val uiState by viewModel.uiState.collectAsState()
    val sidebarWidth by animateDpAsState(if (uiState.isSidebarExpanded) 250.dp else 80.dp)

    // Persistent ViewModels for multi-window/multi-tab persistence
    val usersViewModel: UsersViewModel = viewModel(
        key = "persistent_users_vm",
        factory = viewModelFactory {
            initializer { UsersViewModel() }
        }
    )

    val usersUiState by usersViewModel.uiState.collectAsState()

    // Global Verification Dialog - lives as long as AdminPanelScreen is composed
    if (usersUiState.showCodeVerificationWindow) {
        CodeVerificationDialog(
            title = "Verify code for email:",
            message = "Please enter 6 digit code for ${usersUiState.verificationEmail}",
            viewModel = usersViewModel,
            onDismissRequest = { usersViewModel.setShowCodeVerificationWindow(false) },
            confirmText = "Confirm",
            dismissText = "Cancel",
            width = 500.dp,
            height = 400.dp
        )
    }

    // 1. Adaptive Root Conditional Router
    if (isMobile) {
        MobileAdminLayout(
            selectedTab = uiState.selectedTab,
            onTabSelected = viewModel::selectTab,
            onLogOut = onLogOut
        ) {
            // Mobile Specific Padding Setup
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                MainContent(selectedTab = uiState.selectedTab, onLogOut = onLogOut, usersViewModel = usersViewModel)
            }
        }
    } else {
        // Desktop Layout (Untouched, runs exactly as before)
        Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Sidebar(
                expanded = uiState.isSidebarExpanded,
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::selectTab,
                onToggle = viewModel::toggleSidebar,
                width = sidebarWidth,
                username = uiState.username,
                role = uiState.role
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    MainContent(selectedTab = uiState.selectedTab, onLogOut = onLogOut, usersViewModel = usersViewModel)
                }
            }
        }
    }
}


@Composable
fun MainContent(selectedTab: AdminTab, onLogOut: () -> Unit, usersViewModel: UsersViewModel) {

    when (selectedTab) {
        AdminTab.Dashboard -> {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { DashboardViewModel() }
                }
            )
            DashboardContent(dashboardViewModel)
        }
        AdminTab.Users -> {
            UsersContent(usersViewModel)
        }
        AdminTab.PendingApprovals -> PlaceholderContent("Pending Approvals")
        AdminTab.Groups -> {
            val groupsViewModel: GroupsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { GroupsViewModel() }
                }
            )
            GroupsContent(groupsViewModel)
        }
        AdminTab.Schedules -> PlaceholderContent("Schedules")
        AdminTab.Announcements -> {
            val announcementsViewModel: AnnouncementsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { AnnouncementsViewModel() }
                }
            )
            AnnouncementsContent(announcementsViewModel)
        }
        AdminTab.Notifications -> PlaceholderContent("Notifications")
        AdminTab.ELibrary -> {
            val eLibViewModel: ELibraryViewModel = viewModel(
                factory = viewModelFactory{
                    initializer{ELibraryViewModel()}
                }
            )
            ELibraryContent(eLibViewModel)
        }
        AdminTab.Settings -> {
            SettingsScreen (
                onBackClick = {},
                onLogoutClick = {
                    onLogOut()
                }
            )
        }
        AdminTab.ActivityLogs -> {
            val logsViewModel: ActivityLogsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { ActivityLogsViewModel() }
                }
            )
            ActivityLogsContent(logsViewModel)
        }
    }
}

@Composable
fun Sidebar(
    expanded: Boolean,
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    onToggle: () -> Unit,
    width: Dp,
    username: String,
    role: String
) {
    Column(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.primary)
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
                Text("S", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
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
fun PlaceholderContent(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for $title Coming Soon...", fontSize = 20.sp, color = Color.Gray)
    }
}

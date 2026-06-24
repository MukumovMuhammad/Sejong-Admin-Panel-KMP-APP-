package com.example.AdminPanel.ui.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.AdminPanel.ui.announcements.AnnouncementsContent

enum class AdminTab(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Home),
    Users("Users", Icons.Default.Person),
    PendingApprovals("Pending", Icons.Default.CheckCircle),
    Groups("Groups", Icons.Default.Person),
    Schedules("Schedules", Icons.Default.DateRange),
    Announcements("Announcements", Icons.Default.Notifications),
    Notifications("Alerts", Icons.Default.MailOutline),
    ELibrary("E-Library", Icons.Default.Build),
    ActivityLogs("Logs", Icons.Default.List),
    Settings("Settings", Icons.Default.Settings);
}

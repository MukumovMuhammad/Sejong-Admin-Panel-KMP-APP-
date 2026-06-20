package com.example.AdminPanel.ui.admin

import androidx.lifecycle.ViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StatData(
    val title: String,
    val value: String,
    val change: String,
    val icon: ImageVector
)

data class DashboardUiState(
    val welcomeMessage: String = "Welcome back, Admin!",
    val stats: List<StatData> = listOf(
        StatData("Total Users", "542", "+16 this week", Icons.Default.Person),
        StatData("Students", "426", "+8 this week", Icons.Default.Search),
        StatData("Teachers", "48", "+2 this week", Icons.Default.AccountBox),
        StatData("Pending Approvals", "9", "-3 this week", Icons.Default.Person),
        StatData("Groups", "12", "No change", Icons.Default.List)
    )
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    // Logic for fetching dashboard data would go here
}

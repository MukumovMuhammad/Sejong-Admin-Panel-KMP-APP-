package com.example.AdminPanel.ui.admin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow



data class AdminUiState(
    val isSidebarExpanded: Boolean = true,
    val selectedTab: AdminTab = AdminTab.Dashboard,
    val username: String = "Admin",
    val role: String = "Super Administrator"
)

class AdminViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun toggleSidebar() {
        _uiState.value = _uiState.value.copy(isSidebarExpanded = !_uiState.value.isSidebarExpanded)
    }

    fun selectTab(tab: AdminTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}

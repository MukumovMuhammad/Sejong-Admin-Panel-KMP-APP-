package com.example.AdminPanel.ui.activitylogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.api.AuditApi
import com.example.AdminPanel.data.model.AuditLog
import com.example.AdminPanel.data.model.VisualAuditLog
import com.example.AdminPanel.data.model.getChangesList
import com.example.AdminPanel.data.network.HttpClientFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ActivityLogsUiState(
    val logs: List<VisualAuditLog> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val total: String = "0",
    val hasMore: Boolean = false,
    val currentLimit: Int = 50,
    val currentOffset: Int = 0,
    val selectedAction: String? = null,
    val selectedAdmin: String? = null,
    val selectedModel: String? = null
)

class ActivityLogsViewModel : ViewModel() {
    private val api = AuditApi(HttpClientFactory.create())

    private val _uiState = MutableStateFlow(ActivityLogsUiState())
    val uiState: StateFlow<ActivityLogsUiState> = _uiState.asStateFlow()

    init {
        loadLogs()
    }

    fun loadLogs(
        action: String? = _uiState.value.selectedAction,
        adminUser: String? = _uiState.value.selectedAdmin,
        modelName: String? = _uiState.value.selectedModel,
        refresh: Boolean = true
    ) {
        println("Loading logs for action: $action, admin: $adminUser, model: $modelName")
        viewModelScope.launch {
            val offset = if (refresh) 0 else _uiState.value.currentOffset + _uiState.value.currentLimit
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedAction = action,
                selectedAdmin = adminUser,
                selectedModel = modelName,
                currentOffset = offset
            )

            try {
                val response = api.getAuditLogs(
                    action = action,
                    adminUser = adminUser,
                    modelName = modelName,
                    limit = _uiState.value.currentLimit,
                    offset = offset
                )

                println("Loaded ${response.logs.size} logs for action: $action, admin: $adminUser, model: $modelName")

                // 1. Map raw backend response data into your VisualAuditLog using your custom extension helper!
                val mappedUiLogs = response.logs.map { rawLog ->
                    VisualAuditLog(
                        rawLog = rawLog,
                        changesDisplay = rawLog.getChangesList() // <-- Runs your calculation here once
                    )
                }

                // 2. Combine the new processed list with the existing list safely based on pagination context
                val newLogs = if (refresh) mappedUiLogs else _uiState.value.logs + mappedUiLogs

                _uiState.value = _uiState.value.copy(
                    logs = newLogs,
                    hasMore = response.has_more,
                    isLoading = false
                )
            } catch (e: Exception) {
                println("Error loading logs for action: $action, admin: $adminUser, model: $modelName: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }


    fun setActionFilter(action: String?) {
        val value = if (action == "All Actions") null else action
        // Priority: action > model_name > admin_user. Clear lower priority if setting higher.
        // Or better, following "only one filter at a time", clear others.
        loadLogs(action = value, adminUser = null, modelName = null, refresh = true)
    }

    fun setAdminFilter(admin: String?) {
        val value = if (admin.isNullOrBlank()) null else admin
        loadLogs(action = null, adminUser = value, modelName = null, refresh = true)
    }

    fun setModelFilter(model: String?) {
        val value = if (model == "All Models") null else model
        loadLogs(action = null, adminUser = null, modelName = value, refresh = true)
    }

    fun loadMore() {
        if (!_uiState.value.isLoading && _uiState.value.hasMore) {
            loadLogs(refresh = false)
        }
    }
}

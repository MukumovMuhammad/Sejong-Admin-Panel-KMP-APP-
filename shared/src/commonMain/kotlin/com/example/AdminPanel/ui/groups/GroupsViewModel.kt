package com.example.AdminPanel.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.api.GroupApi
import com.example.AdminPanel.data.model.Group
import com.example.AdminPanel.data.network.HttpClientFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GroupsUiState(
    val groups: List<Group> = emptyList(),
    val filteredGroups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedGroup: Group? = null,
    val searchQuery: String = "",
    val totalGroups: Int = 0,
    val activeGroups: Int = 0,
    val upcomingGroups: Int = 0,
    val totalStudents: Int = 0
)

class GroupsViewModel : ViewModel() {
    private val api = GroupApi(HttpClientFactory.create())

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getGroups()
                val groups = response.groups
                _uiState.value = _uiState.value.copy(
                    groups = groups,
                    filteredGroups = filterGroups(groups, _uiState.value.searchQuery),
                    totalGroups = groups.size,
                    activeGroups = groups.count { it.status == "Active" },
                    upcomingGroups = groups.count { it.status == "Upcoming" },
                    totalStudents = groups.sumOf { it.students_count },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredGroups = filterGroups(_uiState.value.groups, query)
        )
    }

    private fun filterGroups(groups: List<Group>, query: String): List<Group> {
        if (query.isBlank()) return groups
        return groups.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun selectGroup(group: Group?) {
        _uiState.value = _uiState.value.copy(selectedGroup = group)
    }

    fun createGroup(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = api.createGroup(name)
                if (response.error == null) {
                    loadGroups()
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = response.error)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            try {
                api.deleteGroup(groupId)
                loadGroups()
                if (_uiState.value.selectedGroup?.id == groupId) {
                    selectGroup(null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}

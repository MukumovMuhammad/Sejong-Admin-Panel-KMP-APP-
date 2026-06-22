package com.example.AdminPanel.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.api.UserApi
import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.data.network.HttpClientFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UsersUiState(
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(), // Added filtered list
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedUser: User? = null,
    val searchQuery: String = "", // Added search query
    val totalCount: Int = 0,
    val studentsCount: Int = 0,
    val teachersCount: Int = 0,
    val adminsCount: Int = 0,
    val pendingCount: Int = 0
)

class UsersViewModel : ViewModel() {
    private val api = UserApi(HttpClientFactory.create())

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers(status: String? = null, verification: String? = null) {
        println("Loading users!")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getUsers(status = status, verificationStatus = verification)
                println("UserViewModel: Got users data! ${response}")
                _uiState.value = _uiState.value.copy(
                    users = response.users,
                    filteredUsers = filterUsers(response.users, _uiState.value.searchQuery),
                    totalCount = response.total,
                    studentsCount = response.users.count { it.status == "Student" },
                    teachersCount = response.users.count { it.status == "Teacher" },
                    adminsCount = response.users.count { it.status == "Admin" },
                    pendingCount = response.users.count { it.verification_status == "Pending" },
                    isLoading = false
                )
            } catch (e: Exception) {
                println("Got some errors Loading User data! ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredUsers = filterUsers(_uiState.value.users, query)
        )
    }

    private fun filterUsers(users: List<User>, query: String): List<User> {
        if (query.isBlank()) return users
        return users.filter { user ->
            user.fullname?.contains(query, ignoreCase = true) == true ||
            user.username.contains(query, ignoreCase = true) ||
            user.email?.contains(query, ignoreCase = true) == true
        }
    }

    fun selectUser(user: User?) {
        _uiState.value = _uiState.value.copy(selectedUser = user)
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                api.deleteUser(userId)
                loadUsers()
                if (_uiState.value.selectedUser?.id == userId) {
                    selectUser(null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}

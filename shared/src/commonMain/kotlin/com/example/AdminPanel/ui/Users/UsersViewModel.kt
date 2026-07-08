package com.example.AdminPanel.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.api.UserApi
import com.example.AdminPanel.data.model.Announcement
import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.data.network.HttpClientFactory
import com.example.AdminPanel.data.utills.FilterQuery
import com.example.AdminPanel.data.utills.applyGlobalFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UsersUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,

    val error: String? = null,
    val selectedUser: User? = null,
    val searchQuery: String = "", // Added search query
    val totalCount: Int = 0,
    val studentsCount: Int = 0,
    val teachersCount: Int = 0,
    val adminsCount: Int = 0,
    val pendingCount: Int = 0,
    val usersGroups: List<String> = emptyList(),
    val isActionLoading: Boolean = false,
    val actionSuccess: Boolean = false
)

class UsersViewModel : ViewModel() {
    private val api = UserApi(HttpClientFactory.create())

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    val filterQuery = MutableStateFlow(FilterQuery())

    // Complete background pipeline runs effortlessly using a single line
    val filteredUsers = combine(_uiState.map { it.users }, filterQuery) { list, query ->
        list.applyGlobalFilter(query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<User>()
    )


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
                    totalCount = response.total,
                    studentsCount = response.users.count { it.status == "Student" },
                    teachersCount = response.users.count { it.status == "Teacher" },
                    adminsCount = response.users.count { it.status == "Admin" },
                    pendingCount = response.users.count { it.verification_status == "Pending" },
                    usersGroups = response.users
                        .mapNotNull { it.group }
                        .filter { it.isNotBlank() }
                        .distinct(),
                    isLoading = false
                )
            } catch (e: Exception) {
                println("Got some errors Loading User data! ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }




    fun updateFilter(update: (FilterQuery) -> FilterQuery) {
        filterQuery.value = update(filterQuery.value)
    }

    fun selectUser(user: User?) {
        _uiState.value = _uiState.value.copy(selectedUser = user)
    }

    fun approveUser(userId: String) {
        _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
        viewModelScope.launch {
            try {
                val response = api.verifyUser(userId, "approve")
                if (response.error != null) {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true)
                    loadUsers()
                    // If the selected user was the one approved, update it
                    if (_uiState.value.selectedUser?.id == userId) {
                        _uiState.value = _uiState.value.copy(selectedUser = response.user)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun updateUser(userId: String, updates: Map<String, String?>) {
        _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
        viewModelScope.launch {
            try {
                val response = api.editUser(userId, updates)
                if (response.error != null) {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true)
                    loadUsers()
                    if (_uiState.value.selectedUser?.id == userId) {
                        _uiState.value = _uiState.value.copy(selectedUser = response.user)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun deleteUser(userId: String) {
        _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
        viewModelScope.launch {
            try {
                api.deleteUser(userId)
                _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true)
                loadUsers()
                if (_uiState.value.selectedUser?.id == userId) {
                    _uiState.value = _uiState.value.copy(selectedUser = null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun resetActionState() {
        _uiState.value = _uiState.value.copy(error = null, actionSuccess = false, isActionLoading = false)
    }
}

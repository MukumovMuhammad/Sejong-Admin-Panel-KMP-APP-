package com.example.AdminPanel.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.api.UserApi
import com.example.AdminPanel.data.model.Announcement
import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.data.network.HttpClientFactory
import com.example.AdminPanel.data.network.PdfDownloader
import com.example.AdminPanel.data.utills.FilterQuery
import com.example.AdminPanel.data.utills.applyGlobalFilter
import kotlinx.coroutines.Job
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
    val isUserDataLoading: Boolean = false,

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
    val actionSuccess: Boolean = false,
    val message: String? = null,
    val showCodeVerificationWindow: Boolean = false,
    val verificationEmail: String? = null,
    val isDownloadLoading: Boolean = false,
    val pendingUploadFile: ByteArray? = null,
    val pendingUploadFileName: String? = null,
    val showCreateUserDialog: Boolean = false
)

class UsersViewModel : ViewModel() {
    private val api = UserApi(HttpClientFactory.create())

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    private var loadUserJob: Job? = null
    private var loadUsersJob: Job? = null


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
        loadUsersJob?.cancel()
        println("Loading users!")
        loadUsersJob = viewModelScope.launch {
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


    fun loadUser(userid: String){
        println("Loading user ${userid}")
        loadUserJob?.cancel()
        loadUserJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUserDataLoading = true, error = null)
            try {
                val response = api.getUser(userid)
                println("UserViewModel: Got a user data! ${response}")
                _uiState.value = _uiState.value.copy(
                    selectedUser = response.user,
                    isUserDataLoading = false
                )
            } catch (e: Exception) {
                println("Got some errors Loading User data! ${e.message}")
                e.message?.let {
                    if (it.contains("StandaloneCoroutine"))
                    {
                        _uiState.value = _uiState.value.copy( isUserDataLoading = false)
                    }
                    else{
                        _uiState.value = _uiState.value.copy( isUserDataLoading = false, error = e.message)
                    }
                }
            }
        }
    }




    fun updateFilter(update: (FilterQuery) -> FilterQuery) {
        filterQuery.value = update(filterQuery.value)
    }

    fun selectUser(user: User?) {
        if (user == null){
            loadUserJob?.cancel()
            _uiState.value = _uiState.value.copy(
                selectedUser = user,
                isUserDataLoading = false,
                isLoading = false
            )
        }
        _uiState.value = _uiState.value.copy(selectedUser = user)
    }

    fun changeUserAvatar(userId: String, file: ByteArray) {
        _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
        viewModelScope.launch {
            try{
                val response = api.changeUserAvatar(userId, file)
                if (response.error != null) {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true, message = response.message ?: "Avatar changed successfully")
                    loadUser(userId)
                }
            }
            catch (e: Exception){
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }
    fun uploadStudentsByExcel(file: ByteArray) {
        _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false, pendingUploadFile = null, pendingUploadFileName = null)
        viewModelScope.launch {
            try {
                val response = api.uploadStudentsByExcel(file)
                if (response.error != null) {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true, message = response.message ?: "Students imported successfully")
                    loadUsers()
                }
            }
            catch (e: Exception){
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun prepareUpload(file: ByteArray, fileName: String) {
        _uiState.value = _uiState.value.copy(pendingUploadFile = file, pendingUploadFileName = fileName)
    }

    fun cancelUpload() {
        _uiState.value = _uiState.value.copy(pendingUploadFile = null, pendingUploadFileName = null)
    }

    fun confirmUpload() {
        uiState.value.pendingUploadFile?.let {
            uploadStudentsByExcel(it)
        }
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
                _uiState.value = _uiState.value.copy(isActionLoading = false, showCodeVerificationWindow = false ,error = e.message)
            }
        }
    }

    fun emailUserVerify(email: String, code: String){
        _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
        viewModelScope.launch {
            try {
                val response = api.emailVerify(email, code)
                if (response.error != null) {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true, message = response.message )
                    // Hide verification window on success if needed, or let the user see success message
                    _uiState.value = _uiState.value.copy(showCodeVerificationWindow = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun resendVerificationCode(email: String) {
        _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
        viewModelScope.launch {
            try {
                val response = api.resendVerificationCode(email)
                if (response.error != null) {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true, message = response.message)
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
        _uiState.value = _uiState.value.copy(error = null, actionSuccess = false, isActionLoading = false, message = null)
    }

    fun setShowCodeVerificationWindow(show: Boolean, email: String? = null) {
        _uiState.value = _uiState.value.copy(
            showCodeVerificationWindow = show,
            verificationEmail = email ?: _uiState.value.verificationEmail
        )
    }

    fun setShowCreateUserDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateUserDialog = show)
    }

    fun createUser(user: User) {
        _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
        viewModelScope.launch {
            try {
                val response = api.createUser(user)
                if (response.error != null) {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isActionLoading = false, 
                        actionSuccess = true, 
                        showCreateUserDialog = false,
                        message = response.message ?: "User created successfully"
                    )
                    loadUsers()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun downloadImportTemplate(downloader: PdfDownloader) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDownloadLoading = true,
                isActionLoading = true, // Use global overlay
                error = null,
                actionSuccess = false
            )
            try {
                // Use the API to get bytes first to ensure auth token is used
                val bytes = api.downloadImportTemplate()
                val cleanFileName = "students_import_template"
                
                // Then save it via the platform dialog
                val savedPath = downloader.saveXlsxWithDialog(cleanFileName, bytes)
                
                if (savedPath != null) {
                    _uiState.value = _uiState.value.copy(
                        isDownloadLoading = false,
                        isActionLoading = false,
                        actionSuccess = true,
                        message = "Template saved successfully to: $savedPath"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isDownloadLoading = false,
                        isActionLoading = false,
                        error = "Download cancelled or failed to save."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDownloadLoading = false,
                    isActionLoading = false,
                    error = "Failed to download: ${e.message}"
                )
            }
        }
    }
}

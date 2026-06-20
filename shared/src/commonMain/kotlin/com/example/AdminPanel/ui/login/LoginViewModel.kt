package com.example.AdminPanel.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.getConnectivityService
import com.example.AdminPanel.data.api.AuthApi
import com.example.AdminPanel.data.model.ErrorResponse
import com.example.AdminPanel.data.model.LoginResponse
import com.example.AdminPanel.data.network.HttpClientFactory
import com.example.AdminPanel.data.session.SessionManager
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val connectivityService = getConnectivityService()
    private val authApi = AuthApi(HttpClientFactory.create())

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username, error = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun login() {

        val currentState = _uiState.value
        print("LoginViewModel: Login with ${currentState.username} and ${currentState.password}")
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.value = currentState.copy(error = "Username and password cannot be empty")
            return
        }

        if (!connectivityService.isConnected()) {
            _uiState.value = currentState.copy(error = "No internet connection")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)

            try {
                val response = authApi.login(
                    username = currentState.username,
                    password = currentState.password
                )

                when (response.status) {
                    HttpStatusCode.OK -> {
                        println()
                        println("Login fetch the status is ok!")
                        val loginResponse = response.body<LoginResponse>()
                        println("The response is ${loginResponse}")
                        SessionManager.token = loginResponse.token
                        SessionManager.status = loginResponse.status
                        SessionManager.verificationStatus = loginResponse.verificationStatus
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            status = loginResponse.status,
                            verificationStatus = loginResponse.verificationStatus
                        )
                    }
                    HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized -> {
                        val errorResponse = response.body<ErrorResponse>()
                        println("Got a bad request: ${errorResponse}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorResponse.error
                        )
                    }
                    else -> {

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Unexpected error: ${response.status}"
                        )
                        println("Got some errors in login Error: ${response.status}")
                    }
                }
            } catch (e: Exception) {
                println("Got error in login e = ${e}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unknown error occurred"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                val response = authApi.logout()
                if (response.status == HttpStatusCode.OK) {
                    SessionManager.clear()
                    _uiState.value = LoginUiState()
                } else {
                    val errorResponse = response.body<ErrorResponse>()
                    _uiState.value = _uiState.value.copy(error = errorResponse.error)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Logout failed")
            }
        }
    }
}

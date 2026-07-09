package com.example.AdminPanel.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.getConnectivityService
import com.example.AdminPanel.data.api.AuthApi
import com.example.AdminPanel.data.model.simpleMessageResponse
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

    init {
        if (SessionManager.rememberMe){
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isLoggedIn = true,
                status = SessionManager.status,
                verificationStatus = SessionManager.verificationStatus
            )
        }
    }

    fun login(username: String, password:String,rememberUser: Boolean) {

        val currentState = _uiState.value
        println("LoginViewModel: Login with ${username} and ${password}")
        if (username.isBlank() || password.isBlank()) {
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
                    username = username,
                    password = password,
                    device_token = "dvX8Ouj_SxOwHiBqxYSG0t:APA91bEYSTFQIykTLp6HOGwr0hecT1RXciBwFzLBlhV_hyOFhog-SuZn5VYhZFD0ZbdZgxr5jX6bMlDY8Q26feWhZZ4W-z_KGinpiN4kHYjReqtXvI_9nr8"
                )

                when (response.status) {
                    HttpStatusCode.OK -> {
                        println()
                        println("Login fetch the status is ok!")
                        val loginResponse = response.body<LoginResponse>()
                        println("The response is ${loginResponse}")

                        if (loginResponse.status != "Admin"){
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "You are not an Admin! You shall not pass!"
                            )
                            return@launch
                        }

                        SessionManager.rememberMe = rememberUser
                        SessionManager.token = loginResponse.token
                        SessionManager.status = loginResponse.status
                        SessionManager.refresh_token = loginResponse.refresh_token
                        SessionManager.verificationStatus = loginResponse.verificationStatus
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            status = loginResponse.status,
                            verificationStatus = loginResponse.verificationStatus
                        )
                    }
                    HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized -> {
                        val errorResponse = response.body<simpleMessageResponse>()
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
        SessionManager.clearSession()
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isLoggedIn = false,
            status = SessionManager.status,
            verificationStatus = SessionManager.verificationStatus
        )
//        viewModelScope.launch {
//            try {
//                val response = authApi.logout()
//                if (response.status == HttpStatusCode.OK) {
//                    SessionManager.clearSession()
//                    _uiState.value = LoginUiState()
//                } else {
//                    val errorResponse = response.body<ErrorResponse>()
//                    _uiState.value = _uiState.value.copy(error = errorResponse.error)
//                }
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(error = e.message ?: "Logout failed")
//            }
//        }
    }
}

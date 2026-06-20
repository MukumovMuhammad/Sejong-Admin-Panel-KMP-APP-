package com.example.AdminPanel.ui.login


data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val status: String? = null,
    val verificationStatus: String? = null,
)
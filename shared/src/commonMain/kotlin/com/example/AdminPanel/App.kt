package com.example.AdminPanel

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.AdminPanel.ui.admin.AdminPanelScreen
import com.example.AdminPanel.ui.login.LoginScreen
import com.example.AdminPanel.ui.admin.AdminViewModel
import com.example.AdminPanel.ui.login.LoginViewModel

enum class Screen {
    Login, AdminPanel
}

@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.Login) }

        when (currentScreen) {
            Screen.Login -> {
                val loginViewModel: LoginViewModel = viewModel { LoginViewModel() }
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        currentScreen = Screen.AdminPanel
                    }
                )
            }
            Screen.AdminPanel -> {
                val adminViewModel: AdminViewModel = viewModel { AdminViewModel() }
                AdminPanelScreen(viewModel = adminViewModel)
            }
        }
    }
}

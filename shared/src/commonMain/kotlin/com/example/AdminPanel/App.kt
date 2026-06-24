package com.example.AdminPanel

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.AdminPanel.ui.admin.AdminPanelScreen
import com.example.AdminPanel.ui.login.LoginScreen
import com.example.AdminPanel.ui.admin.AdminViewModel
import com.example.AdminPanel.ui.login.LoginViewModel

import com.example.AdminPanel.ui.theme.AdminPanelTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay

enum class Screen {
    Login, AdminPanel
}

@Composable
fun App(mobile: Boolean = false) {
    AdminPanelTheme {
        var currentScreen by remember { mutableStateOf(Screen.Login) }
        val loginViewModel: LoginViewModel = viewModel(
            factory = viewModelFactory {
                initializer { LoginViewModel() }
            }
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.Login -> {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            currentScreen = Screen.AdminPanel
                        },
                        mobile = mobile
                    )
                }
                Screen.AdminPanel -> {
                    val adminViewModel: AdminViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer { AdminViewModel() }
                        }
                    )
                    AdminPanelScreen(viewModel = adminViewModel, onLogOut = {
                        loginViewModel.logout()
                        currentScreen = Screen.Login
                    })
                }
            }
        }
    }
}



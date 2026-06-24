package com.example.AdminPanel.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.getConnectivityService
import com.example.AdminPanel.data.session.SessionManager
import com.example.AdminPanel.ui.login.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.AdminPanel.ui.components.*

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit, isMobile: Boolean = false) {
    val uiState by viewModel.uiState.collectAsState()

    var rememberChecked by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {


        if (!isMobile){
            // Left Side: Branding
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primary), // Use Theme Primary
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(48.dp)) {
                    // Logo
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color.White, shape = RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("S", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 48.sp)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    HeaderText(
                        "Admin Panel",
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BodyText(
                        "Manage users, classes, schedules, announcements and more.",
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }


        // Right Side: Login Form
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.2f)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        )
        {
            Column(
                modifier = Modifier
                    .widthIn(max = 440.dp)
                    .padding(48.dp),
                horizontalAlignment = Alignment.Start
            ) {
                HeaderText("Welcome back!")
                SubHeaderText(
                    "Sign in to continue to Sejong Admin Panel",
                    modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
                )

                AppTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    placeholder = "Enter your username",
                    leadingIcon = Icons.Default.Email,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppPasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Enter your password",
                    leadingIcon = Icons.Default.Lock,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberChecked,
                            onCheckedChange = { rememberChecked = it }
                        )
                        Text("Remember me", style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(onClick = {}) {
                        Text("Forgot password?", color = MaterialTheme.colorScheme.primary)
                    }
                }

                if (uiState.error != null) {
                    Text(
                        uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                PrimaryButton(
                    text = "Log In",
                    onClick = { viewModel.login(username, password, rememberChecked) },
                    isLoading = uiState.isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(" Secure login with JWT authentication", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}


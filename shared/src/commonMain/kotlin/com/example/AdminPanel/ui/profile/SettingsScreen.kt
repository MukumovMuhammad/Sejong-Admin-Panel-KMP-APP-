package com.example.AdminPanel.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.AdminPanel.ui.components.AppDialog
import com.example.AdminPanel.ui.components.SettingsCard
import com.example.AdminPanel.ui.components.SettingsItem
import com.example.AdminPanel.ui.components.SettingsSectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {

    var showLogoutDialog by remember { mutableStateOf(false) }

// 2. Put the dialog call outside your lists or buttons
    if (showLogoutDialog) {
        AppDialog(
            title = "Log Out",
            message = "Do you want to Log Out?",
            onClose = { showLogoutDialog = false }, // Hide it when closed
            onOkClick = {
                onLogoutClick()
                showLogoutDialog = false // Hide it after logging out
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                SettingsSectionHeader("Account")
                SettingsCard {
                    SettingsItem(
                        title = "Profile Information",
                        subtitle = "Name, Email, Phone number",
                        icon = Icons.Default.Person
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        title = "Password & Security",
                        subtitle = "Change password, 2FA",
                        icon = Icons.Default.Lock
                    )
                }
            }

            item {
                SettingsSectionHeader("App Settings")
                SettingsCard {
                    SettingsItem(
                        title = "Notifications",
                        subtitle = "Manage alerts and sounds",
                        icon = Icons.Default.Notifications
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    var isDarkMode by remember { mutableStateOf(false) }
                    SettingsItem(
                        title = "Dark Mode",
                        subtitle = if (isDarkMode) "Enabled" else "Disabled",
                        icon = Icons.Default.Build, // Placeholder for Brightness
                        trailing = {
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { isDarkMode = it }
                            )
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        title = "Language",
                        subtitle = "English (US)",
                        icon = Icons.Default.Info // Placeholder for Language
                    )
                }
            }

            item {
                SettingsSectionHeader("Support")
                SettingsCard {
                    SettingsItem(
                        title = "Help Center",
                        icon = Icons.Default.Info
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        title = "Contact Support",
                        icon = Icons.Default.Share // Placeholder for Mail
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        title = "Privacy Policy",
                        icon = Icons.AutoMirrored.Filled.List // Placeholder for Description
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        showLogoutDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

package com.example.AdminPanel.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.ui.components.AppPasswordTextField
import com.example.AdminPanel.ui.components.AppTextField
import com.example.AdminPanel.ui.components.FilterDropdown
import com.example.AdminPanel.ui.components.PrimaryButton
import com.example.AdminPanel.ui.components.SecondaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserDialog(
    onDismiss: () -> Unit,
    onCreate: (User) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Student") }
    var group by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(16.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.widthIn(max = 500.dp).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Create New User",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Form Fields
                AppTextField(
                    value = fullname,
                    onValueChange = { fullname = it },
                    label = "Full Name",
                    placeholder = "Enter full name"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    placeholder = "Enter username"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppPasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Enter password"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "Enter email address"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "Phone Number",
                    placeholder = "Enter phone number"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = "Date of Birth",
                    placeholder = "YYYY-MM-DD"
                )
                Spacer(modifier = Modifier.height(12.dp))

                FilterDropdown(
                    label = "Role / Status",
                    options = listOf("Student", "Teacher", "Admin", "Guest"),
                    selectedOption = status,
                    onOptionSelected = { status = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = group,
                    onValueChange = { group = it },
                    label = "Group (Optional)",
                    placeholder = "Enter group name"
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = "Create User",
                        onClick = {
                            val newUser = User(
                                id = "", // Server will generate
                                username = username,
                                password = password,
                                email = email,
                                fullname = fullname,
                                phone_number = phoneNumber,
                                date_of_birth = dob,
                                status = status,
                                group = group,
                                verification_status = "Approved"
                            )
                            onCreate(newUser)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = username.isNotBlank() && password.isNotBlank() && email.isNotBlank()
                    )
                }
            }
        }
    }
}

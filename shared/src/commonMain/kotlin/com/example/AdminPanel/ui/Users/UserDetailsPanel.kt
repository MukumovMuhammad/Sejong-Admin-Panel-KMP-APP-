package com.example.AdminPanel.ui.users

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.data.utills.getFormattedTimeOfPost
import com.example.AdminPanel.ui.components.*
import com.example.AdminPanel.ui.theme.BrandBlue
import com.example.AdminPanel.ui.theme.BrandRed
import com.example.AdminPanel.ui.theme.Success
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch

@Composable
fun UserDetailsPanel(
    user: User,
    viewModel: UsersViewModel,
    isUserDataLoading: Boolean,
    onClose: () -> Unit,
    onDelete: (User) -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val (formattedDate, formattedTime) = user.date_joined.getFormattedTimeOfPost()
    var selectedTab by remember { mutableStateOf(UserTabs.PERINFO) }
    var isEditable by remember(user.id) { mutableStateOf(false) }

    var showAvatarPreview by remember { mutableStateOf(false) }
    var showEmailWorning by remember { mutableStateOf(false) }

    val updates = remember(user.id) { mutableMapOf<String, String?>() }
    // Editable fields
    var fullname by remember(user.id) { mutableStateOf(user.fullname) }
    var email by remember(user.id) { mutableStateOf(user.email) }
    var phoneNumber by remember(user.id) { mutableStateOf(user.phone_number) }
    var dob by remember(user.id) { mutableStateOf(user.date_of_birth) }
    var status by remember(user.id) { mutableStateOf(user.status) }
    var verification_status by remember(user.id) { mutableStateOf(user.verification_status) }
    var password: String by remember(user.id) { mutableStateOf("") }
    var device_token: String by remember(user.id) { mutableStateOf("") }

    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    val scope = rememberCoroutineScope()



    LaunchedEffect(user.id){
        viewModel.loadUser(user.id)
    }

    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image, mode = PickerMode.Single
    ) { image ->
        if (image != null) {
            scope.launch {
                selectedImage =  image.readBytes()
                viewModel.changeUserAvatar(user.id, selectedImage!!)
                selectedImage = null
            }
        }
    }

    if (showEmailWorning){
        println("New update data is ${updates}")
        AppDialog(
            title = "Change Email?",
            message = "The email address for ${user.email} will be updated to $email. To apply this change, a 6-digit confirmation code will be sent to the new email address ($email) for verification.",
            onClose = {showEmailWorning = false},
            onOkClick = {

                println("The user is edited! New data is ${updates}")
                viewModel.setShowCodeVerificationWindow(true, email)
                if (!updates.isNullOrEmpty()) {
                    viewModel.updateUser(user.id, updates)
                }

                isEditable = false
            },
            confirmText = "Ok",
            dismissText = "Cancel",
            isDanger = true
        )
    }

    // CodeVerificationDialog moved to AdminPanelScreen
    DetailPanelLayout(
        title = "User Details",
        onClose = onClose,
        footerContent = {
            if (isEditable) {
                Button(
                    onClick = {

                        if (fullname != user.fullname) updates["fullname"] = fullname

                        if (phoneNumber != user.phone_number) updates["phone_number"] = phoneNumber
                        if (dob != user.date_of_birth) updates["date_of_birth"] = dob
                        if (status != user.status) updates["status"] = status
                        if (password.isNotEmpty()) updates["password"] = password
                        if (device_token != user.device_token) updates["device_token"] = device_token


                        if (email != user.email){
                            showEmailWorning = true
                            updates["email"] = email
                            return@Button
                        }
                        if (updates.isNotEmpty()) {
                            println("The user is edited! New data is ${updates}")
                            viewModel.updateUser(user.id, updates)
                        }

                        isEditable = false
                    },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { isEditable = false },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandRed)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            } else {
                if (user.verification_status == "Pending") {
                    Button(
                        onClick = { viewModel.approveUser(user.id) },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Success)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Approve User", fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { isEditable = true },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BrandBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlue)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile", fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { onDelete(user) },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete User")
                }
            }
        }
    ) {
        // --- PROFILE HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {



            HoverableImage(
                avatarUrl = if (selectedImage != null) selectedImage else user.avatar,
                Mymodifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentDescription = "Avatar",
                ImageOnHover = if(isEditable) Icons.Default.Edit else Icons.Default.Check
            ){
                if (isEditable){
                    launcher.launch()
                }
                else{
                    showAvatarPreview = true
                }
            }

            if (showAvatarPreview) {
                ImagePreviewDialog(
                    imageUrl = user.avatar,
                    title = "Profile Photo",
                    width = 500.dp,
                    height = 500.dp,
                    onDismissRequest = { showAvatarPreview = false }
                )
            }

            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        user.fullname ?: "No Name", 
                        style = MaterialTheme.typography.headlineSmall, 
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
                Text(user.username, style = MaterialTheme.typography.bodyMedium, color = BrandBlue, fontWeight = FontWeight.Medium)
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(user.status)
                    VerificationBadge(user.verification_status)
                }
            }
        }

        // --- TABS ---
        AnimatedContentTabs(
            tabs = UserTabs.entries.toTypedArray(),
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            labelProvider = { it.label }
        ) { tab ->
            when (tab) {
                UserTabs.PERINFO -> {
                    DetailSection("Personal Information") {
                        if (isEditable) {
                            AppTextField(value = fullname, onValueChange = { fullname = it }, label = "Full Name", placeholder = "Enter full name")
                            AppTextField(value = email, onValueChange = { email = it }, label = "Email Address", placeholder = "Enter email", keyboardType = KeyboardType.Email)
                            AppTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = "Phone Number", placeholder = "Enter phone")
                            AppTextField(value = dob, onValueChange = { dob = it }, label = "Date of Birth", placeholder = "YYYY-MM-DD")
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                            AppTextField(value = password, onValueChange = { password = it }, label = "New Password", placeholder = "leave empty for no change")
                        } else {
                            DetailRow("Full Name", user.fullname, isLoading = isUserDataLoading) { clipboardManager.setText(AnnotatedString(user.fullname.toString()))}
                            DetailRow("Email Address", user.email, isLoading = isUserDataLoading){clipboardManager.setText(AnnotatedString(user.email.toString()))}
                            DetailRow("Phone Number", user.phone_number, isLoading = isUserDataLoading){clipboardManager.setText(AnnotatedString(user.phone_number.toString()))}
                            DetailRow("Date of Birth", user.date_of_birth , isLoading = isUserDataLoading){clipboardManager.setText(AnnotatedString(user.date_of_birth.toString()))}
                            DetailRow("Registration Date", "$formattedDate at $formattedTime", isLastRow = true, isLoading = isUserDataLoading){clipboardManager.setText(AnnotatedString(user.date_joined.toString()))}
                        }
                    }
                }
                UserTabs.ACADINFO -> {
                    DetailSection("Academic Profile") {
                        if (isEditable) {
                            FilterDropdown(
                                label = "Assign Role/Status",
                                options = listOf("Student", "Teacher", "Admin", "Guest"),
                                selectedOption = status,
                                onOptionSelected = { status = it }
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            FilterDropdown(
                                label = "Assign Verification",
                                options = listOf("Pending", "Approved", "Rejected"),
                                selectedOption = verification_status,
                                onOptionSelected = { verification_status = it  }
                            )

                        }
                        DetailRow("Primary Role", user.status, isLoading = isUserDataLoading) { clipboardManager.setText(AnnotatedString(user.status.toString())) }
                        DetailRow("Verification", user.verification_status,isLoading = isUserDataLoading){clipboardManager.setText(AnnotatedString(user.status.toString()))}
                        DetailRow("Assigned Group", user.group ?: "Not Assigned", isLastRow = true,isLoading = isUserDataLoading){clipboardManager.setText(AnnotatedString(user.group.toString()))}
                    }
                }
                UserTabs.DEVICEINFO -> {
                    DetailSection("Device Information") {

                        if(isEditable){
                            AppTextField(value = device_token?: "", onValueChange = { device_token = it }, label = "Device Token", placeholder = "Enter device token")
                        }
                        else{
                            DetailRow("Device Token", user.device_token, isLastRow = true, isLoading = isUserDataLoading) {clipboardManager.setText(AnnotatedString(user.device_token.toString()))}
                        }
                    }
                }
            }
        }
    }
}

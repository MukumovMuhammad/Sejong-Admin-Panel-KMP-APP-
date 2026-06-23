package com.example.AdminPanel.ui.users

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.ui.components.*

@Composable
fun UsersContent(viewModel: UsersViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var panelWidth by remember { mutableStateOf(450.dp) }

    var searchText by remember { mutableStateOf("") }
    var statusSelected by remember { mutableStateOf("") }
    var verificationSelected by remember { mutableStateOf("") }
    var groupSelected by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 0.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp), // 👈 Gives space around the entire top bar
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Header Text (Takes up all available remaining space)
                Column(
                    modifier = Modifier.weight(1f) // 👈 CRITICAL: This pushes your buttons to the absolute right side
                ) {
                    Text("Dashboard > Users", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    HeaderText("Users Management")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("View, manage and organize all users in the system.", color = Color.Gray, fontSize = 14.sp)
                }

                // Right Side: Action Buttons Container
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // 👈 Handles the space between each button perfectly
                ) {
                    SecondaryButton(
                        text = "Import Students",
                        onClick = {},
                        modifier = Modifier.width(160.dp),
                        icon = Icons.Default.Share
                    )

                    PrimaryButton(
                        text = "Add User",
                        onClick = {},
                        modifier = Modifier.width(140.dp),
                        icon = Icons.Default.Add
                    )

                    IconButton(onClick = { viewModel.loadUsers() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            }



            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard("Total Users", uiState.totalCount.toString(), "", Icons.Default.Person, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Students", uiState.studentsCount.toString(), "", Icons.Default.AccountBox, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Teachers", uiState.teachersCount.toString(), "", Icons.Default.Person, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Admins", uiState.adminsCount.toString(), "", Icons.Default.Lock, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Pending", uiState.pendingCount.toString(), "", Icons.Default.Refresh, Modifier.weight(1f), isDanger = true, isLoading = uiState.isLoading)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Filters Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {

                Column{
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search users by name, username, email...") },
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(8.dp),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true
                        )
                    }


                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    )
                    {
                        FilterDropdown(
                            label = "Status",
                            options = listOf("All Statuses", "Student", "Guest", "Teacher", "Admin"),
                            selectedOption = statusSelected.ifEmpty { "All Statuses" },
                            onOptionSelected = { choice ->
                                statusSelected = if (choice == "All Statuses") "" else choice
                            },
                            modifier = Modifier.weight(0.8f)
                        )

                        // 2. Verification Filter
                        FilterDropdown(
                            label = "Verification",
                            options = listOf("All Verifications", "Approved", "Pending", "Rejected"),
                            selectedOption = verificationSelected.ifEmpty { "All Verifications" },
                            onOptionSelected = { choice ->
                                verificationSelected = if (choice == "All Verifications") "" else choice
                            },
                            modifier = Modifier.weight(0.8f)
                        )

                        // 3. Group Filter
                        FilterDropdown(
                            label = "Group",
                            options = listOf("All Groups") + uiState.usersGroups + "no groups",
                            selectedOption = groupSelected.ifEmpty { "All Groups" },
                            onOptionSelected = { choice ->
                                groupSelected = if (choice == "All Groups") "" else choice
                            },
                            modifier = Modifier.weight(0.8f)
                        )

                        // 4. Clear Filters Interaction Button
                        TextButton(
                            onClick = {
                                searchText = ""
                                statusSelected = ""
                                verificationSelected = ""
                                groupSelected = ""
                            }
                        ) {
                            Text(
                                text = "Clear Filters",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }


                    }
                }



            }

            Spacer(modifier = Modifier.height(16.dp))

            // Table Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("User", modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Username", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Status", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Verification", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Group", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Joined At", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Actions", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }

            // List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                if (uiState.isLoading && uiState.users.isEmpty()) {
                    items(10) {
                        UserRow(
                            user = User(id = "", username = "", status = "", verification_status = ""),
                            isSelected = false,
                            isLoading = true,
                            onClick = {},
                            onDelete = {}
                        )
                    }
                } else {
                    val filteredUsers = uiState.users.filter { user ->
                        // 1. Text Search Filter (Fullname, Username, or Email)
                        val matchesSearch = searchText.isEmpty() ||
                                user.fullname?.contains(searchText, ignoreCase = true) == true ||
                                user.username.contains(searchText, ignoreCase = true) ||
                                user.email?.contains(searchText, ignoreCase = true) == true

                        // 2. Status Dropdown Filter
                        val matchesStatus = statusSelected.isEmpty() ||
                                statusSelected == "All Statuses" || // Optional: if you have an "All" option
                                user.status.equals(statusSelected, ignoreCase = true)

                        // 3. Verification Dropdown Filter
                        val matchesVerification = verificationSelected.isEmpty() ||
                                verificationSelected == "All" ||
                                user.verification_status.equals(verificationSelected, ignoreCase = true)

                        // 4. Group Dropdown Filter

                        var matchesGroup = groupSelected.isEmpty() ||
                                groupSelected == "All Groups" ||
                                user.group?.equals(groupSelected, ignoreCase = true) == true

                        if (groupSelected == "no groups"){
                            matchesGroup =  user.group.isNullOrEmpty()
                        }
                        // Only keep the user if they satisfy ALL active conditions
                        matchesSearch && matchesStatus && matchesVerification && matchesGroup
                    }

                    if (filteredUsers.size < 1){
                        item{
                            EmptyStateComponent(
                                title = "Nothing was found!",
                                icon = Icons.Default.Info,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                    items(filteredUsers) { user ->
                        UserRow(
                            user = user,
                            isSelected = uiState.selectedUser?.id == user.id,
                            isLoading = uiState.isLoading,
                            onClick = { viewModel.selectUser(user) },
                            onDelete = { viewModel.deleteUser(user.id) }
                        )
                    }
                }
            }
        }


        if (uiState.selectedUser != null){
            // Overlay Side Panel Layer
            AnimatedVisibility(
                visible = uiState.selectedUser != null,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Row(modifier = Modifier.width(panelWidth).fillMaxHeight()) {
                    // Resize Handle (Draggable Area)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(4.dp)
                            .background(Color.Transparent)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    panelWidth = (panelWidth - dragAmount.x.toDp()).coerceIn(350.dp, 800.dp)
                                }
                            }
                    )

                    UserDetailsPanel(
                        user = uiState.selectedUser!!,
                        onClose = { viewModel.selectUser(null) }
                    )
                }
            }
        }

    }
}

@Composable
fun UserRow(
    user: User,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    isLoading: Boolean = false
) {

    var formattedDate = ""
    var formattedTime = ""

    if (!user.date_joined.isNullOrBlank()) {
        val parts = user.date_joined.split(" ")
        if (parts.size >= 2) {
            val datePart = parts[0]
            val timePart = parts[1]
            val timePieces = timePart.split(":")
            val datePieces = datePart.split("-")
            if (datePieces.size >= 3) formattedDate = "${datePieces[0]}/${datePieces[1]}/${datePieces[2]}"
            if (timePieces.size >= 2) formattedTime = "${timePieces[0]}/${timePieces[1]}"
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(enabled = !isLoading, onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 2.dp else 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Info
            Row(modifier = Modifier.weight(2.5f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .shimmerLoadingAnimation(isLoading)
                ) {
                    if (!isLoading) {
                        AsyncImage(
                            model = user.avatar,
                            contentDescription = "Image Avatar",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    if (isLoading) {
                        Box(modifier = Modifier.width(100.dp).height(14.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.width(130.dp).height(11.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                    } else {
                        Text(user.fullname ?: "No Name", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(user.email ?: "", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            // Username
            Box(modifier = Modifier.weight(1.5f)) {
                if (isLoading) {
                    Box(modifier = Modifier.width(80.dp).height(14.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                } else {
                    Text(user.username, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Status
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    Box(modifier = Modifier.width(60.dp).height(20.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).shimmerLoadingAnimation(true))
                } else {
                    StatusBadge(user.status)
                }
            }

            // Verification
            Box(modifier = Modifier.weight(1.2f)) {
                if (isLoading) {
                    Box(modifier = Modifier.width(70.dp).height(20.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).shimmerLoadingAnimation(true))
                } else {
                    VerificationBadge(user.verification_status)
                }
            }

            // Group
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    Box(modifier = Modifier.width(40.dp).height(14.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                } else {
                    Text(user.group ?: "-", fontSize = 13.sp)
                }
            }

            // Joined At
            Column(modifier = Modifier.weight(1.5f)) {
                if (isLoading) {
                    Box(modifier = Modifier.width(75.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(50.dp).height(11.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                } else {
                    Text(formattedDate, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(formattedTime, fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Actions
            Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.Center) {
                if (isLoading) {
                    repeat(3) {
                        Box(modifier = Modifier.padding(horizontal = 4.dp).size(24.dp).background(Color.LightGray.copy(alpha = 0.3f), CircleShape).shimmerLoadingAnimation(true))
                    }
                } else {
                    IconButton(onClick = onClick) { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Red.copy(alpha = 0.7f)) }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "Student" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "Teacher" -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        "Admin" -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
        else -> Color(0xFFF5F5F5) to Color(0xFF616161)
    }
    Surface(
        color = color.first,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            status, 
            color = color.second, 
            fontSize = 10.sp, 
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun VerificationBadge(status: String) {
    val (bgColor, textColor, icon) = when (status) {
        "Approved" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.CheckCircle)
        "Pending" -> Triple(Color(0xFFFFF3E0), Color(0xFFFF9800), Icons.Default.Refresh)
        "Rejected" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.Close)
        else -> Triple(Color(0xFFF5F5F5), Color(0xFF616161), Icons.Default.Info)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = textColor)
        Spacer(modifier = Modifier.width(4.dp))
        Text(status, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

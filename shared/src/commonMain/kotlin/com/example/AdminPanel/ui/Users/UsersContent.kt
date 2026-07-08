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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.data.utills.getFormattedTimeOfPost
import com.example.AdminPanel.ui.components.*
import com.example.AdminPanel.ui.theme.*

@Composable
fun UsersContent(viewModel: UsersViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var panelWidth by remember { mutableStateOf(450.dp) }

    val filteredUsers by viewModel.filteredUsers.collectAsState()
    val filterQuery by viewModel.filterQuery.collectAsState()

    var userToDelete by remember { mutableStateOf<User?>(null) }

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
                    modifier = Modifier.weight(1f)
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
                            value = filterQuery.search,
                            onValueChange = { text->
                                viewModel.updateFilter{it.copy(search = text)}
                            },
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
                            label = filterQuery.category,
                            options = listOf("All Statuses", "Student", "Guest", "Teacher", "Admin"),
                            selectedOption = filterQuery.category.ifEmpty { "All Statuses" },
                            onOptionSelected = { choice ->
                                viewModel.updateFilter{it.copy(category = if (choice == "All Statuses") "" else choice)}
                            },
                            modifier = Modifier.weight(0.8f)
                        )

                        // 2. Verification Filter
                        FilterDropdown(
                            label = filterQuery.subCategory,
                            options = listOf("All Verifications", "Approved", "Pending", "Rejected"),
                            selectedOption = filterQuery.subCategory.ifEmpty { "All Verifications" },
                            onOptionSelected = { choice ->
                                viewModel.updateFilter{it.copy(subCategory = if (choice == "All Verifications") "" else choice)}

                            },
                            modifier = Modifier.weight(0.8f)
                        )

                        // 3. Group Filter
                        FilterDropdown(
                            label = filterQuery.group,
                            options = listOf("All Groups") + uiState.usersGroups + "no groups",
                            selectedOption = filterQuery.group.ifEmpty { "All Groups" },
                            onOptionSelected = { choice ->
                                viewModel.updateFilter{it.copy(group = if (choice == "All Groups") "" else choice)}

                            },
                            modifier = Modifier.weight(0.8f)
                        )

                        // 4. Clear Filters Interaction Button
                        TextButton(
                            onClick = {
                                viewModel.updateFilter{it.copy(
                                    group = "All Groups",
                                    search = "",
                                    category = "All status",
                                    subCategory = "All verifications"
                                )}
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
                            onApprove = {},
                            onDelete = {}
                        )
                    }
                } else {

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
                            onApprove = { viewModel.approveUser(user.id) },
                            onDelete = { userToDelete = user }
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
                            .pointerHoverIcon(PointerIcon.Hand)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    panelWidth = (panelWidth - dragAmount.x.toDp()).coerceIn(350.dp, 800.dp)
                                }
                            }
                    )

                    UserDetailsPanel(
                        user = uiState.selectedUser!!,
                        viewModel = viewModel,
                        onClose = { viewModel.selectUser(null) },
                        onDelete = { userToDelete = uiState.selectedUser }
                    )
                }
            }
        }

        if (userToDelete != null){
            AppDialog(
                title = "Delete User?",
                message = "Are you sure you want to delete '${userToDelete?.fullname}'? This action cannot be undone.",
                onClose = { userToDelete = null },
                onOkClick = {
                    userToDelete?.id?.let { viewModel.deleteUser(it) }
                    userToDelete = null
                },
                confirmText = "Delete",
                isDanger = true
            )
        }


        ActionStatusDialog(
            isLoading = uiState.isActionLoading,
            isSuccess = uiState.actionSuccess,
            error = uiState.error,
            onDismiss = { viewModel.resetActionState() }
        )

    }
}

@Composable
fun UserRow(
    user: User,
    isSelected: Boolean,
    onClick: () -> Unit,
    onApprove: () -> Unit,
    onDelete: () -> Unit,
    isLoading: Boolean = false
) {

    val (formattedDate,formattedTime ) = user.date_joined.getFormattedTimeOfPost()

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
            Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                if (isLoading) {
                    repeat(3) {
                        Box(modifier = Modifier.padding(horizontal = 4.dp).size(24.dp).background(Color.LightGray.copy(alpha = 0.3f), CircleShape).shimmerLoadingAnimation(true))
                    }
                } else {
                    // Button 1: Approve (if Pending) OR Edit
                    if (user.verification_status == "Pending") {
                        IconButton(
                            onClick = onApprove,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Success)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Approve", modifier = Modifier.size(20.dp))
                        }
                    } else {
                        IconButton(onClick = onClick) { 
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp), tint = Color.Gray) 
                        }
                    }
                    
                    // Button 2: Details/Info
                    IconButton(onClick = onClick) { 
                        Icon(Icons.Default.Info, contentDescription = "Details", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) 
                    }
                    
                    // Button 3: Delete
                    IconButton(onClick = onDelete) { 
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp), tint = Color.Red.copy(alpha = 0.7f)) 
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "Student" -> Success.copy(alpha = 0.1f) to Success
        "Teacher" -> BrandBlue.copy(alpha = 0.1f) to BrandBlue
        "Admin" -> BrandRed.copy(alpha = 0.1f) to BrandRed
        else -> Color(0xFFF1F5F9) to Color(0xFF64748B)
    }
    Surface(
        color = color.first,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            status, 
            color = color.second, 
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun VerificationBadge(status: String) {
    val (bgColor, textColor, icon) = when (status) {
        "Approved" -> Triple(Success.copy(alpha = 0.1f), Success, Icons.Default.CheckCircle)
        "Pending" -> Triple(Warning.copy(alpha = 0.1f), Warning, Icons.Default.Refresh)
        "Rejected" -> Triple(BrandRed.copy(alpha = 0.1f), BrandRed, Icons.Default.Close)
        else -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), Icons.Default.Info)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = textColor)
        Spacer(modifier = Modifier.width(6.dp))
        Text(status, color = textColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

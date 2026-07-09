package com.example.AdminPanel.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.example.AdminPanel.ui.users.UsersViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun ImagePreviewDialog(
    imageUrl: String?,
    onDismissRequest: () -> Unit,
    title: String,
    width: Dp,
    height: Dp
) {
    // Android specific implementation
}

@Composable
actual fun CodeVerificationDialog(
    title: String,
    message: String,
    viewModel: UsersViewModel,
    onDismissRequest: () -> Unit,
    confirmText: String,
    dismissText: String,
    width: Dp,
    height: Dp
) {
    // Android specific implementation
}

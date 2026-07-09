package com.example.AdminPanel.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import coil3.compose.AsyncImage
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val dialogState = rememberWindowState(width = width, height = height)

    val customIcon = try {
        useResource("sejong_logo.png") { inputStream ->
            BitmapPainter(loadImageBitmap(inputStream))
        }
    } catch (e: Exception) {
        null
    }

    Window(
        onCloseRequest = onDismissRequest,
        state = dialogState,
        title = title,
        icon = customIcon
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Scroll) {
                                val delta = event.changes.first().scrollDelta.y
                                scale = (scale - delta * 0.1f).coerceIn(0.5f, 5.0f)
                            }
                        }
                    }
                }
        ) {
            WindowDraggableArea {
                Box(modifier = Modifier.fillMaxSize())
            }

            val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
                scale = (scale * zoomChange).coerceIn(0.5f, 5.0f)
                if (scale > 1f) offset += offsetChange else offset = Offset.Zero
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .transformable(state = transformState),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(0.9f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
    var codeText by remember { mutableStateOf("") }
    val maxCodeLength = 6
    val dialogState = rememberWindowState(width = width, height = height)
    var showCloseWarning by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()

    val customIcon = try {
        useResource("sejong_logo.png") { inputStream ->
            BitmapPainter(loadImageBitmap(inputStream))
        }
    } catch (e: Exception) {
        null
    }

    Window(
        onCloseRequest = { showCloseWarning = true },
        state = dialogState,
        title = title,
        icon = customIcon,
        resizable = true 
    ) {
        if (showCloseWarning) {
            AppDialog(
                title = "Cancel Verification?",
                message = "Are you sure you want to close this window? Any unsaved email changes will not be applied.",
                onClose = { showCloseWarning = false },
                onOkClick = { 
                    showCloseWarning = false
                    onDismissRequest() 
                },
                confirmText = "Yes, Close",
                dismissText = "Stay",
                isDanger = true
            )
        }



        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Header Icon
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "#",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🛠️ The 6-Digit Integrated Field Engine
                    BasicTextField(
                        value = codeText,
                        onValueChange = { newValue ->
                            if (newValue.length <= maxCodeLength && newValue.all { it.isDigit() }) {
                                codeText = newValue
                            }
                        },
                        enabled = !uiState.isActionLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        decorationBox = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(maxCodeLength) { index ->
                                    val char = codeText.getOrNull(index)?.toString() ?: ""
                                    val isFocused = codeText.length == index

                                    Box(
                                        modifier = Modifier
                                            .size(width = 46.dp, height = 56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                            .border(
                                                width = if (isFocused) 2.dp else 1.dp,
                                                color = if (isFocused) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = char,
                                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryButton(
                            text = dismissText,
                            onClick = { showCloseWarning = true },
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isActionLoading
                        )
                        PrimaryButton(
                            text = confirmText,
                            enabled = codeText.length == maxCodeLength && !uiState.isActionLoading,
                            onClick = {
                                uiState.verificationEmail?.let { email ->
                                    viewModel.emailUserVerify(email, codeText)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resend Code Link/Button
                    TextButton(
                        onClick = {
                            uiState.verificationEmail?.let { email ->
                                viewModel.resendVerificationCode(email)
                            }
                        },
                        enabled = !uiState.isActionLoading
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Didn't receive a code? Resend")
                        }
                    }
                }

                // Loading Overlay
                if (uiState.actionSuccess){
                    ActionStatusDialog(
                        isLoading = uiState.isActionLoading,
                        isSuccess = uiState.actionSuccess,
                        error = uiState.error,
                        onDismiss = { viewModel.resetActionState() }
                    )
                }
                if (uiState.isActionLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }


            }
        }
    }
}

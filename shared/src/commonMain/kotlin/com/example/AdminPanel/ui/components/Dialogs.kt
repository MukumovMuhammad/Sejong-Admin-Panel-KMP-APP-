package com.example.AdminPanel.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDialog(
    title: String = "Alert",
    message: String,
    onClose: () -> Unit,
    onOkClick: () -> Unit,
    confirmText: String = "OK",
    dismissText: String = "Close",
    isDanger: Boolean = false
) {
    BasicAlertDialog(
        onDismissRequest = onClose,
        modifier = Modifier
            .widthIn(max = 400.dp)
            .padding(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(24.dp)) {
                // X Close Icon in top right corner
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 12.dp, y = (-12).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header Accent Icon
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDanger) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "!", 
                                style = MaterialTheme.typography.headlineMedium,
                                color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryButton(
                            text = dismissText,
                            onClick = onClose,
                            modifier = Modifier.weight(1f)
                        )
                        PrimaryButton(
                            text = confirmText,
                            onClick = {
                                onOkClick()
                                onClose()
                            },
                            modifier = Modifier.weight(1f),
                            containerColor = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionStatusDialog(
    isLoading: Boolean,
    isSuccess: Boolean,
    error: String?,
    onDismiss: () -> Unit
) {
    if (isLoading || isSuccess || error != null) {
        BasicAlertDialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(24.dp).widthIn(max = 300.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Processing...", fontWeight = FontWeight.Bold)
                    } else if (isSuccess) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle, 
                            contentDescription = null, 
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Success!", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        PrimaryButton(text = "OK", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
                    } else if (error != null) {
                        Icon(
                            imageVector = Icons.Default.Warning, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Error", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(error, textAlign = TextAlign.Center, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        PrimaryButton(text = "Close", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
expect fun ImagePreviewDialog(
    imageUrl: String?,
    onDismissRequest: () -> Unit,
    title: String,
    width: Dp,
    height: Dp
)
package com.example.AdminPanel.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import coil3.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun ImagePreviewDialog(
    imageUrl: String?,
    onDismissRequest: () -> Unit,
    title: String,
    width: Dp,
    height: Dp
) {
    val dialogState = rememberDialogState(width = width, height = height)
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    DialogWindow(
        onCloseRequest = onDismissRequest,
        state = dialogState,
        title = title,
        undecorated = true,
        transparent = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                // Stable Desktop Scroll-to-Zoom engine
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
            // Allows moving the dialog window by dragging the black background area
            WindowDraggableArea {
                Box(modifier = Modifier.fillMaxSize())
            }

            // Trackpad Pinch-to-zoom and Drag panning setup
            val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
                scale = (scale * zoomChange).coerceIn(0.5f, 5.0f)
                if (scale > 1f) offset += offsetChange else offset = Offset.Zero
            }

            // Image Container Layer
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
                // Replace this with your specific AsyncImage component
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(0.9f)
                )
            }

            // Floating Custom Close Button
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}
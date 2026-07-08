package com.example.AdminPanel.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit // Or any icon you need
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun HoverableImage(
    avatarUrl: String?,
    Mymodifier: Modifier,
    contentDescription: String,
    ImageOnHover: ImageVector,
    onIconClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = Mymodifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isHovered = true
                            PointerEventType.Exit -> isHovered = false
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Base layer: The Avatar Image
        AsyncImage(
            model = avatarUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )

        // Top layer: Hover overlay with an animated transition
        AnimatedVisibility(
            visible = isHovered,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { onIconClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageOnHover,
                    contentDescription = contentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
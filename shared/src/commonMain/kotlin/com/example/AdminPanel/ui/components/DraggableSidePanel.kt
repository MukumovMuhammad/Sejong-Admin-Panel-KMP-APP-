package com.example.AdminPanel.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DraggableSidePanel(
    panelWidth: Dp,
    onWidthChange: (Dp) -> Unit,
    modifier: Modifier = Modifier,
    minWidth: Dp = 350.dp,
    maxWidth: Dp = 800.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Row(
        modifier = modifier
            .width(panelWidth)
            .fillMaxHeight()
    ) {
        // --- MINIMALIST RESIZE HANDLE ---
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp) // Invisible padding area to comfortably grab with a mouse pointer
                .background(Color.Transparent)
                // 🌟 Changes mouse cursor to a native two-sided resize arrow on Desktop/Web
                .pointerHoverIcon(PointerIcon.Hand)

                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val updatedWidth = (panelWidth - dragAmount.x.toDp())
                        onWidthChange(updatedWidth.coerceIn(minWidth, maxWidth))
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Simple, native-looking 1dp divider line (No glows, no pills, no clutter)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }

        // --- SHEET CONTAINER CANVAS ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            content()
        }
    }
}
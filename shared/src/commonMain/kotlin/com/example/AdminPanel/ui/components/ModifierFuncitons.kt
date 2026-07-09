package com.example.AdminPanel.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.shimmerIfMissing(value: String?, isLoading: Boolean): Modifier = composed {
    val showShimmer = isLoading && (value.isNullOrEmpty())
    if (showShimmer) {
        this
            .clip(RoundedCornerShape(4.dp))
            .shimmerLoadingAnimation(true)
    } else {
        this
    }
}

fun Modifier.shimmerLoadingAnimation(
    isLoading: Boolean,
    baseColor: Color = Color(0xFFE2E8F0), // Light slate gray
    highlightColor: Color = Color(0xFFF1F5F9) // Lighter slate
): Modifier = composed {
    if (!isLoading) return@composed this

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f, // Increased for better coverage on larger screens
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )

    val shimmerColors = listOf(
        baseColor,
        highlightColor,
        baseColor
    )

    this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnim.value - 600f, y = translateAnim.value - 600f),
            end = Offset(x = translateAnim.value, y = translateAnim.value)
        )
    )
}

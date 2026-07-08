package com.example.AdminPanel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AdminPanel.ui.theme.BrandBlue
import com.example.AdminPanel.ui.theme.BrandRed


@Composable
fun DetailPanelLayout(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    footerContent: @Composable (RowScope.() -> Unit)? = null,
    bodyContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // High-end light gray canvas background
    ) {
        // --- FIXED HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrandBlue // Strict Logo Blue
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = BrandRed // Strict Logo Red
                )
            }
        }

        // Faint Crimson brand border accent line below header
        HorizontalDivider(color = BrandRed.copy(alpha = 0.2f), thickness = 1.dp)

        // --- SCROLLABLE CONTAINER ---
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            bodyContent()
        }

        // --- ACTION FOOTER ---
        if (footerContent != null) {
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = footerContent
            )
        }
    }
}
package com.example.AdminPanel.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileAdminLayout(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    onLogOut: () -> Unit,
    content: @Composable () -> Unit
) {
    var showMoreSheet by remember { mutableStateOf(false) }

    // Top 3 primary screens shown instantly on the mobile bottom bar
    val primaryTabs = listOf(AdminTab.Dashboard, AdminTab.Users, AdminTab.Announcements)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedTab.title) },
                actions = {
                    IconButton(onClick = onLogOut) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Log Out", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                primaryTabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        label = { Text(tab.title) },
                        icon = { Icon(tab.icon, contentDescription = tab.title) }
                    )
                }

                // The 4th item opens the remaining 7 tabs
                NavigationBarItem(
                    selected = selectedTab !in primaryTabs,
                    onClick = { showMoreSheet = true },
                    label = { Text("More") },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "More Options") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            content()
        }

        // Expanded Bottom Grid for the remaining admin modules
        if (showMoreSheet) {
            ModalBottomSheet(onDismissRequest = { showMoreSheet = false }) {
                Column(modifier = Modifier.padding(16.dp).padding(bottom = 24.dp)) {
                    Text("All Management Tools", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))

                    // Display all 10 tabs cleanly in a wrap grid layout
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 3
                    ) {
                        AdminTab.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    onTabSelected(tab)
                                    showMoreSheet = false
                                },
                                label = { Text(tab.title) },
                                leadingIcon = { Icon(tab.icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                modifier = Modifier.height(44.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.AdminPanel.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AdminPanel.data.model.Announcement
import com.example.AdminPanel.ui.components.*

@Composable
fun AnnouncementsContent(viewModel: AnnouncementsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Breadcrumbs & Title
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text("Dashboard > Announcements", color = Color.Gray, fontSize = 12.sp)
            HeaderText("Announcements")
        }

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard("Total Announcements", uiState.totalCount.toString(), "+3 this month", Icons.Default.Info, Modifier.weight(1f))
            StatCard("Published", uiState.publishedCount.toString(), "+5 this month", Icons.Default.Send, Modifier.weight(1f))
            StatCard("Drafts", uiState.draftsCount.toString(), "No change", Icons.Default.Edit, Modifier.weight(1f))
            StatCard("Deleted", uiState.deletedCount.toString(), "+1 this month", Icons.Default.Delete, Modifier.weight(1f), isDanger = true)
            
            // Add Announcement Button Card
            Surface(
                onClick = { showAddDialog = true },
                modifier = Modifier.weight(0.8f).height(100.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Text("Add Announcement", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Filters Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search by title or author...") },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                
                FilterDropdown("All Languages", Modifier.weight(1f))
                FilterDropdown("All Status", Modifier.weight(1f))
                FilterDropdown("Select date range", Modifier.weight(1f), icon = Icons.Default.DateRange)
                
                IconButton(onClick = { viewModel.loadAnnouncements() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Table Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Title (Russian)", modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Author", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Posted At", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Status", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Actions", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
        }

        // List
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(uiState.announcements) { announcement ->
                    AnnouncementRow(
                        announcement = announcement,
                        onDelete = { viewModel.deleteAnnouncement(announcement.id ?: "") }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddAnnouncementSidePanel(
                onDismiss = { showAddDialog = false },
                onConfirm = { titles, contents ->
                    viewModel.createAnnouncement(
                        titleRus = titles[0],
                        titleTaj = titles[1],
                        titleEng = titles[2],
                        titleKor = titles[3],
                        contentRus = contents[0],
                        contentTaj = contents[1],
                        contentEng = contents[2],
                        contentKor = contents[3]
                    )
                    showAddDialog = false
                }
            )
        }
    }
}


@Composable
fun FilterDropdown(label: String, modifier: Modifier, icon: ImageVector? = null) {
    OutlinedCard(
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 13.sp, color = Color.Gray)
            Icon(icon ?: Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun AnnouncementRow(announcement: Announcement, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Thumbnail
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
            ) {
                // Image loading logic would go here
            }
            Spacer(modifier = Modifier.width(16.dp))
            
            // Title Multi-lang
            Column(modifier = Modifier.weight(2.5f)) {
                Text(announcement.title_rus ?: "", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(announcement.title_kor ?: "", fontSize = 11.sp, color = Color.Gray)
                Text(announcement.title_eng ?: "", fontSize = 11.sp, color = Color.Gray)
            }

            // Author
            Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).background(Color.LightGray, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(announcement.author ?: "Unknown", fontSize = 12.sp)
            }

            // Time
            Column(modifier = Modifier.weight(1.2f)) {
                Text("15 Jun 2026", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text("10:30 AM", fontSize = 11.sp, color = Color.Gray)
            }

            // Status
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Published", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            // Actions
            Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = {}) { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
                IconButton(onClick = {}) { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Red.copy(alpha = 0.7f)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnnouncementSidePanel(
    onDismiss: () -> Unit,
    onConfirm: (List<String>, List<String>) -> Unit
) {
    var titleRus by remember { mutableStateOf("") }
    var titleTaj by remember { mutableStateOf("") }
    var titleEng by remember { mutableStateOf("") }
    var titleKor by remember { mutableStateOf("") }
    
    var contentRus by remember { mutableStateOf("") }
    var contentTaj by remember { mutableStateOf("") }
    var contentEng by remember { mutableStateOf("") }
    var contentKor by remember { mutableStateOf("") }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth().padding(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(32.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    HeaderText("Add New Announcement")
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Titles Section
                Text("Titles (At least one of title_rus or title_taj is required)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(value = titleRus, onValueChange = { titleRus = it }, label = "Title (Russian) *", placeholder = "Введите заголовок", modifier = Modifier.weight(1f))
                    AppTextField(value = titleTaj, onValueChange = { titleTaj = it }, label = "Title (Tajik)", placeholder = "Сарлавҳа ба тоҷикӣ", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(value = titleEng, onValueChange = { titleEng = it }, label = "Title (English)", placeholder = "Enter title in English", modifier = Modifier.weight(1f))
                    AppTextField(value = titleKor, onValueChange = { titleKor = it }, label = "Title (Korean)", placeholder = "제목을 입력하세요", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Contents Section
                Text("Contents", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(value = contentRus, onValueChange = { contentRus = it }, label = "Content (Russian)", placeholder = "Введите текст", modifier = Modifier.weight(1f), singleLine = false)
                    AppTextField(value = contentTaj, onValueChange = { contentTaj = it }, label = "Content (Tajik)", placeholder = "Матни эълон", modifier = Modifier.weight(1f), singleLine = false)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(value = contentEng, onValueChange = { contentEng = it }, label = "Content (English)", placeholder = "Enter text", modifier = Modifier.weight(1f), singleLine = false)
                    AppTextField(value = contentKor, onValueChange = { contentKor = it }, label = "Content (Korean)", placeholder = "내용을 입력하세요", modifier = Modifier.weight(1f), singleLine = false)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Images Section
                Text("Images (Optional)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        Text("Click to upload images", color = Color.Gray, fontSize = 12.sp)
                        Text("JPEG, PNG, WEBP up to 2MB each. Max 10 images.", color = Color.Gray.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SecondaryButton(text = "Cancel", onClick = onDismiss, modifier = Modifier.weight(1f))
                    PrimaryButton(
                        text = "Create Announcement",
                        onClick = {
                            onConfirm(
                                listOf(titleRus, titleTaj, titleEng, titleKor),
                                listOf(contentRus, contentTaj, contentEng, contentKor)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = titleRus.isNotBlank() || titleTaj.isNotBlank()
                    )
                }
            }
        }
    }
}

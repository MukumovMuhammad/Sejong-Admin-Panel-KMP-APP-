package com.example.AdminPanel.ui.announcements

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.AdminPanel.data.model.Announcement
import com.example.AdminPanel.data.utills.getFormattedTimeOfPost
import com.example.AdminPanel.ui.components.*
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch

@Composable
fun AnnouncementDetailsPanel(
    announcement: Announcement,
    onClose: () -> Unit,
    onUpdate: (String, List<String>, List<String>, List<ByteArray>?) -> Unit
) {
    var isEditable by remember { mutableStateOf(false) }

    // The engine for your animated tabs
    var currentLangTab by remember { mutableStateOf(AppLanguage.RUS) }

    var titleRus by remember(announcement) { mutableStateOf(announcement.title_rus ?: "") }
    var titleTaj by remember(announcement) { mutableStateOf(announcement.title_taj ?: "") }
    var titleEng by remember(announcement) { mutableStateOf(announcement.title_eng ?: "") }
    var titleKor by remember(announcement) { mutableStateOf(announcement.title_kor ?: "") }

    var contentRus by remember(announcement) { mutableStateOf(announcement.content_rus ?: "") }
    var contentTaj by remember(announcement) { mutableStateOf(announcement.content_taj ?: "") }
    var contentEng by remember(announcement) { mutableStateOf(announcement.content_eng ?: "") }
    var contentKor by remember(announcement) { mutableStateOf(announcement.content_kor ?: "") }

    var selectedImages by remember { mutableStateOf<List<ByteArray>?>(null) }
    val scope = rememberCoroutineScope()

    val (data, time) = announcement.time_posted.getFormattedTimeOfPost()

    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image,
        mode = PickerMode.Multiple()
    ) { files ->
        if (files != null) {
            scope.launch { selectedImages = files.map { it.readBytes() } }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderText("Announcement Details")
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = null) }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // --- SCROLLABLE CONTENT ---
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Metadata
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Author: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(announcement.author ?: "Unknown", fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Posted: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("$data at $time", fontSize = 13.sp)
                        }
                    }
                }

                // Edit Toggle Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Translation Settings", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = if (isEditable) "Editing Unlocked" else "Read Only", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = isEditable, onCheckedChange = { isEditable = it }) // Replace with your PrimarySwitchButton
                    }
                }

                ContentTabs(
                    tabs = AppLanguage.values(),
                    selectedTab = currentLangTab,
                    onTabSelected = { currentLangTab = it },
                    labelProvider = { it.label }
                )

                // 🌟 The Reusable Animation Engine for your text fields
                AnimatedContent(
                    targetState = currentLangTab,
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                    },
                    label = "Language Fields Animation"
                ) { targetLanguage ->
                    // Inside the animation, we show only the fields for the active language
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        when (targetLanguage) {
                            AppLanguage.RUS -> {
                                AppTextField(isReadOnly = !isEditable, value = titleRus, onValueChange = { titleRus = it }, label = "Title (Russian)", placeholder = "Enter Russian title", modifier = Modifier.fillMaxWidth())
                                AppTextField(isReadOnly = !isEditable, value = contentRus, onValueChange = { contentRus = it }, label = "Content (Russian)", placeholder = "Enter Russian content", modifier = Modifier.fillMaxWidth(), singleLine = false)
                            }
                            AppLanguage.TAJ -> {
                                AppTextField(isReadOnly = !isEditable, value = titleTaj, onValueChange = { titleTaj = it }, label = "Title (Tajik)", placeholder = "Enter Tajik title", modifier = Modifier.fillMaxWidth())
                                AppTextField(isReadOnly = !isEditable, value = contentTaj, onValueChange = { contentTaj = it }, label = "Content (Tajik)", placeholder = "Enter Tajik content", modifier = Modifier.fillMaxWidth(), singleLine = false)
                            }
                            AppLanguage.ENG -> {
                                AppTextField(isReadOnly = !isEditable, value = titleEng, onValueChange = { titleEng = it }, label = "Title (English)", placeholder = "Enter English title", modifier = Modifier.fillMaxWidth())
                                AppTextField(isReadOnly = !isEditable, value = contentEng, onValueChange = { contentEng = it }, label = "Content (English)", placeholder = "Enter English content", modifier = Modifier.fillMaxWidth(), singleLine = false)
                            }
                            AppLanguage.KOR -> {
                                AppTextField(isReadOnly = !isEditable, value = titleKor, onValueChange = { titleKor = it }, label = "Title (Korean)", placeholder = "Enter Korean title", modifier = Modifier.fillMaxWidth())
                                AppTextField(isReadOnly = !isEditable, value = contentKor, onValueChange = { contentKor = it }, label = "Content (Korean)", placeholder = "Enter Korean content", modifier = Modifier.fillMaxWidth(), singleLine = false)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- IMAGES SECTION ---
                Text("Images", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                if (selectedImages == null && announcement.images.isNotEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        announcement.images.take(5).forEach { image ->
                            AsyncImage(
                                model = image.url,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }

                if (isEditable) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .clickable { launcher.launch() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Click to replace images", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                }

                AnimatedVisibility(visible = selectedImages != null) {
                    Text(
                        text = "${selectedImages?.size} new images selected ready for upload.",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- FOOTER BUTTONS ---
            Surface(
                tonalElevation = 12.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SecondaryButton(text = "Cancel", onClick = onClose, modifier = Modifier.weight(1f))
                    PrimaryButton(
                        text = "Save Changes",
                        onClick = {
                            onUpdate(
                                announcement.id ?: "",
                                listOf(titleRus, titleTaj, titleEng, titleKor),
                                listOf(contentRus, contentTaj, contentEng, contentKor),
                                selectedImages
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isEditable && (titleRus.isNotBlank() || titleTaj.isNotBlank())
                    )
                }
            }
        }
    }
}

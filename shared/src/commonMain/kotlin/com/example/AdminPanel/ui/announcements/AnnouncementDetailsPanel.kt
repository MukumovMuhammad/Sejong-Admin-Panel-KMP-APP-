package com.example.AdminPanel.ui.announcements


import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.AdminPanel.ui.theme.BrandBlue

@Composable
fun AnnouncementDetailsPanel(
    announcement: Announcement,
    onClose: () -> Unit,
    onUpdate: (String, List<String>, List<String>, List<ByteArray>?) -> Unit
) {
    var isEditable by remember { mutableStateOf(false) }
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
    val (date, time) = announcement.time_posted.getFormattedTimeOfPost()

    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image, mode = PickerMode.Multiple()
    ) { files ->
        if (files != null) {
            scope.launch { selectedImages = files.map { it.readBytes() } }
        }
    }

    DetailPanelLayout(
        title = "Announcement Details",
        onClose = onClose,
        footerContent = {
            // --- THE RESTORED SWITCH TOGGLE ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Switch(
                    checked = isEditable,
                    onCheckedChange = { isEditable = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BrandBlue,
                        checkedTrackColor = BrandBlue.copy(alpha = 0.3f)
                    )
                )
                Text(
                    text = if (isEditable) "Editing Enabled" else "Read-Only Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isEditable) BrandBlue else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onUpdate(
                        announcement.id ?: "",
                        listOf(titleRus, titleTaj, titleEng, titleKor),
                        listOf(contentRus, contentTaj, contentEng, contentKor),
                        selectedImages
                    )
                },
                enabled = isEditable && (titleRus.isNotBlank() || titleTaj.isNotBlank()),
                modifier = Modifier.width(140.dp).height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlue, // 🔵 Logo Blue Button
                    contentColor = Color.White
                )
            ) {
                Text("Save Changes", fontSize = 13.sp)
            }
        }
    ) {
        // --- METADATA OVERVIEW CARD ---
        DetailSection("System Properties") {
            DetailRow("Author / Publisher", announcement.author ?: "System Admin")
            DetailRow("Timestamp Posted", "$date at $time", isLastRow = true)
        }

        // --- TRANSLATION TAB INPUT ENGINE ---
        AnimatedContentTabs(
            tabs = AppLanguage.entries.toTypedArray(),
            selectedTab = currentLangTab,
            onTabSelected = { currentLangTab = it },
            labelProvider = { it.label }
        ) { targetLanguage ->
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                when (targetLanguage) {
                    AppLanguage.RUS -> {
                        AppTextField(isReadOnly = !isEditable, value = titleRus, onValueChange = { titleRus = it }, label = "Title (Russian)", placeholder = "Enter Russian title")
                        AppTextField(isReadOnly = !isEditable, value = contentRus, onValueChange = { contentRus = it }, label = "Content (Russian)", placeholder = "Enter Russian content", singleLine = false, minLines = 4)
                    }
                    AppLanguage.TAJ -> {
                        AppTextField(isReadOnly = !isEditable, value = titleTaj, onValueChange = { titleTaj = it }, label = "Title (Tajik)", placeholder = "Enter Tajik title")
                        AppTextField(isReadOnly = !isEditable, value = contentTaj, onValueChange = { contentTaj = it }, label = "Content (Tajik)", placeholder = "Enter Tajik content", singleLine = false, minLines = 4)
                    }
                    AppLanguage.ENG -> {
                        AppTextField(isReadOnly = !isEditable, value = titleEng, onValueChange = { titleEng = it }, label = "Title (English)", placeholder = "Enter English title")
                        AppTextField(isReadOnly = !isEditable, value = contentEng, onValueChange = { contentEng = it }, label = "Content (English)", placeholder = "Enter English content", singleLine = false, minLines = 4)
                    }
                    AppLanguage.KOR -> {
                        AppTextField(isReadOnly = !isEditable, value = titleKor, onValueChange = { titleKor = it }, label = "Title (Korean)", placeholder = "Enter Korean title")
                        AppTextField(isReadOnly = !isEditable, value = contentKor, onValueChange = { contentKor = it }, label = "Content (Korean)", placeholder = "Enter Korean content", singleLine = false, minLines = 4)
                    }
                }
            }
        }

        // --- ATTACHED MEDIA GALLERY ---
        DetailSection("Attached Assets") {
            if (selectedImages == null && announcement.images.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    announcement.images.take(4).forEach { image ->
                        AsyncImage(
                            model = image.url,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(6.dp))
                        )
                    }
                }
            }

            if (isEditable) {
                OutlinedButton(
                    onClick = { launcher.launch() },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Replace Media Files Collection", fontSize = 12.sp)
                }
            }
        }
    }
}
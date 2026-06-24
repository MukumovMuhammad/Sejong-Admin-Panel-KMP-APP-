package com.example.AdminPanel.ui.announcements

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
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
    var isEdittble by remember{mutableStateOf(false)}
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

    val (data,time) = announcement.time_posted.getFormattedTimeOfPost()

    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image,
        mode = PickerMode.Multiple()
    ) { files ->
        if (files != null) {
            scope.launch {
                selectedImages = files.map { it.readBytes() }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderText("Announcement Details")
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = null) }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                        Text("${data} at ${time}" ?: "-", fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Titles Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
            Text("Titles", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Edit",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    PrimarySwitchButton(
                        checked = isEdittble,
                        onCheckedChange = { isEdittble = it }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(isReadOnly = !isEdittble,value = titleRus, onValueChange = { titleRus = it }, label = "Title (Russian)", placeholder = "Enter Russian title", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(isReadOnly = !isEdittble,value = titleTaj, onValueChange = { titleTaj = it }, label = "Title (Tajik)", placeholder = "Enter Tajik title", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(isReadOnly = !isEdittble,value = titleEng, onValueChange = { titleEng = it }, label = "Title (English)", placeholder = "Enter English title", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(isReadOnly = !isEdittble,value = titleKor, onValueChange = { titleKor = it }, label = "Title (Korean)", placeholder = "Enter Korean title", modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))

            // Contents Section
            Text("Contents", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(isReadOnly = !isEdittble,value = contentRus, onValueChange = { contentRus = it }, label = "Content (Russian)", placeholder = "Enter Russian content", modifier = Modifier.fillMaxWidth(), singleLine = false)
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(isReadOnly = !isEdittble,value = contentTaj, onValueChange = { contentTaj = it }, label = "Content (Tajik)", placeholder = "Enter Tajik content", modifier = Modifier.fillMaxWidth(), singleLine = false)
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(isReadOnly = !isEdittble,value = contentEng, onValueChange = { contentEng = it }, label = "Content (English)", placeholder = "Enter English content", modifier = Modifier.fillMaxWidth(), singleLine = false)
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(isReadOnly = !isEdittble,value = contentKor, onValueChange = { contentKor = it }, label = "Content (Korean)", placeholder = "Enter Korean content", modifier = Modifier.fillMaxWidth(), singleLine = false)

            Spacer(modifier = Modifier.height(24.dp))

            // Images Section
            Text("Images", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            
            if (selectedImages == null && announcement.images.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    announcement.images.take(5).forEach { image ->
                        AsyncImage(
                            model = image.url,
                            contentDescription = null,
                            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }


            if (isEdittble){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { launcher.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        Text("Click to replace images", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }


            AnimatedVisibility(visible = selectedImages != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${selectedImages?.size} new images selected.",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    enabled = titleRus.isNotBlank() || titleTaj.isNotBlank()
                )
            }
        }
    }
}

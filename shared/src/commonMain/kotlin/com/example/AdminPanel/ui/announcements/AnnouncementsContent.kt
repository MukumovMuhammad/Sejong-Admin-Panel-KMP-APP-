package com.example.AdminPanel.ui.announcements


import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.AdminPanel.data.model.Announcement
import com.example.AdminPanel.data.utills.getFormattedTimeOfPost
import com.example.AdminPanel.ui.components.*
import com.example.AdminPanel.ui.users.UserDetailsPanel
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import kotlin.text.ifEmpty
import kotlin.time.Instant


@Composable
fun AnnouncementsContent(viewModel: AnnouncementsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var announcementToDelete by remember { mutableStateOf<Announcement?>(null) }
    var panelWidth by remember { mutableStateOf(500.dp) }

    var timeFilterLabel by remember { mutableStateOf("All Time") }

    val filteredAnn by viewModel.filteredAnn.collectAsState()
    val filterQuery by viewModel.filterQuery.collectAsState()


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dashboard > Announcements", color = Color.Gray, fontSize = 12.sp)
                    HeaderText("Announcements")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    PrimaryButton(
                        text = "Add Announcement",
                        onClick = { showAddDialog = true },
                        modifier = Modifier.width(300.dp),
                        icon = Icons.Default.Add
                    )

                    IconButton(onClick = { viewModel.loadAnnouncements() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }

                }
            }




            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard("Total Announcements", uiState.totalCount.toString(), "", Icons.Default.Info, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Published", uiState.publishedCount.toString(), "", Icons.Default.Send, Modifier.weight(1f),isLoading = uiState.isLoading)
                StatCard("Drafts", uiState.draftsCount.toString(), "", Icons.Default.Edit, Modifier.weight(1f),isLoading = uiState.isLoading)
                StatCard("Deleted", uiState.deletedCount.toString(), "", Icons.Default.Delete, Modifier.weight(1f), isDanger = true, isLoading = uiState.isLoading)

            }

            Spacer(modifier = Modifier.height(24.dp))

            // Filters Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {

                Column{
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = filterQuery.search,
                            onValueChange = { text ->
                                viewModel.updateFilter { it.copy(search = text) }
                           },
                            placeholder = { Text("Search by title ...") },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(8.dp),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )

                        // Inside your filter row container:
                        TimeRangeDropdown(
                            selectedLabel = timeFilterLabel,
                            onPresetSelected = { preset ->
                                timeFilterLabel = preset
                                // Clear manual timestamps if a quick preset is clicked
                                viewModel.updateFilter { it.copy(startDate = null, endDate = null) }
                            },
                            onCustomRangeSelected = { start, end ->
                                viewModel.updateFilter { it.copy(startDate = start, endDate = end) }
                                if (start != null && end != null) {
                                    // Format timestamps into a display label (e.g., "Jun 10 - Jun 17")
                                    timeFilterLabel = "Custom Range Selected"
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                if (uiState.isLoading && uiState.announcements.isEmpty()){
                    items(10) {
                        AnnouncementRow(
                            announcement = Announcement(),
                            isLoading = true,
                            onClick = {},
                            onDelete = { }
                        )
                    }
                }
                else if (uiState.error != null && uiState.announcements.isEmpty()){
                   item{
                       Text("Error loading announcements: ${uiState.error}")
                   }
                }
                else{

                    if (filteredAnn.isEmpty()){
                        item{
                            EmptyStateComponent(
                                title = "Nothing was found!",
                                icon = Icons.Default.Info,
                            )
                        }
                    }
                    else{
                        items(filteredAnn) { announcement ->
                            AnnouncementRow(
                                announcement = announcement,
                                isSelected = uiState.selectedAnnouncement?.id == announcement.id,
                                isLoading = uiState.isLoading,
                                onClick = { viewModel.selectAnnouncement(announcement) },
                                onDelete = { announcementToDelete = announcement }
                            )
                        }
                    }

                }

            }
        }

        if (uiState.selectedAnnouncement != null){
            // Side Panel for Details/Edit
            AnimatedVisibility(
                visible = uiState.selectedAnnouncement != null,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Row(modifier = Modifier.width(panelWidth).fillMaxHeight()) {
                    // Resize Handle (Draggable Area)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(4.dp)
                            .background(Color.Transparent)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    panelWidth = (panelWidth - dragAmount.x.toDp()).coerceIn(350.dp, 800.dp)
                                }
                            }
                    )
                    AnnouncementDetailsPanel(
                        announcement = uiState.selectedAnnouncement!!,
                        onClose = { viewModel.selectAnnouncement(null) },
                        onUpdate = { id, titles, contents, images ->
                            viewModel.updateAnnouncement(
                                id = id,
                                titleRus = titles[0],
                                titleTaj = titles[1],
                                titleEng = titles[2],
                                titleKor = titles[3],
                                contentRus = contents[0],
                                contentTaj = contents[1],
                                contentEng = contents[2],
                                contentKor = contents[3],
                                images = images
                            )
                        }
                    )
                }
            }

        }


        if (showAddDialog) {
            AddAnnouncementSidePanel(
                onDismiss = { showAddDialog = false },
                onConfirm = { titles, contents, images ->
                    viewModel.createAnnouncement(
                        titleRus = titles[0],
                        titleTaj = titles[1],
                        titleEng = titles[2],
                        titleKor = titles[3],
                        contentRus = contents[0],
                        contentTaj = contents[1],
                        contentEng = contents[2],
                        contentKor = contents[3],
                        images = images
                    )
                    showAddDialog = false
                }
            )
        }

        // Delete Confirmation Dialog
        if (announcementToDelete != null) {
            AppDialog(
                title = "Delete Announcement?",
                message = "Are you sure you want to delete '${announcementToDelete?.title_rus}'? This action cannot be undone.",
                onClose = { announcementToDelete = null },
                onOkClick = {
                    announcementToDelete?.id?.let { viewModel.deleteAnnouncement(it) }
                    announcementToDelete = null
                },
                confirmText = "Delete",
                isDanger = true
            )
        }

        // Action Status Dialog (Loading, Success, Error)
        ActionStatusDialog(
            isLoading = uiState.isActionLoading,
            isSuccess = uiState.actionSuccess,
            error = uiState.error,
            onDismiss = { viewModel.resetActionState() }
        )
    }
}




@Composable
fun AnnouncementRow(
    announcement: Announcement?,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onDelete: () -> Unit,
    isLoading: Boolean = false
) {
    val (formattedDate, formattedTime) = announcement?.time_posted.getFormattedTimeOfPost()

    var imageUrl: String? = if (!announcement?.images.isNullOrEmpty() && !announcement?.images[0]?.url.isNullOrEmpty()) {
            announcement.images[0].url
        } else {
            null
        }


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(enabled = !isLoading, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 2.dp else 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Thumbnail
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .shimmerLoadingAnimation(isLoading)
            ) {
                if (!isLoading) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Announcement image",
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title Multi-lang
            Column(modifier = Modifier.weight(2.5f)) {
                if (isLoading) {
                    Box(modifier = Modifier.width(160.dp).height(14.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(100.dp).height(11.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(120.dp).height(11.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                } else {
                    Text(announcement?.title_rus ?: "", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(announcement?.title_kor ?: "", fontSize = 11.sp, color = Color.Gray)
                    Text(announcement?.title_eng ?: "", fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Author
            Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f), CircleShape)
                        .shimmerLoadingAnimation(isLoading)
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (isLoading) {
                    Box(modifier = Modifier.width(60.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                } else {
                    Text(announcement?.author ?: "Unknown", fontSize = 12.sp)
                }
            }

            // Time
            Column(modifier = Modifier.weight(1.2f)) {
                if (isLoading) {
                    Box(modifier = Modifier.width(70.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(45.dp).height(11.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                } else {
                    Text(formattedDate, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(formattedTime, fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Status Badge
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    Box(modifier = Modifier.width(70.dp).height(20.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).shimmerLoadingAnimation(true))
                } else {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Published", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }

            // Actions Buttons
            Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.Center) {
                if (isLoading) {
                    repeat(3) {
                        Box(modifier = Modifier.padding(horizontal = 4.dp).size(24.dp).background(Color.LightGray.copy(alpha = 0.3f), CircleShape).shimmerLoadingAnimation(true))
                    }
                } else {
                    IconButton(onClick = onClick) { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onClick) { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Red.copy(alpha = 0.7f)) }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnnouncementSidePanel(
    onDismiss: () -> Unit,
    onConfirm: (List<String>, List<String>, List<ByteArray>) -> Unit
) {
    var titleRus by remember { mutableStateOf("") }
    var titleTaj by remember { mutableStateOf("") }
    var titleEng by remember { mutableStateOf("") }
    var titleKor by remember { mutableStateOf("") }
    
    var contentRus by remember { mutableStateOf("") }
    var contentTaj by remember { mutableStateOf("") }
    var contentEng by remember { mutableStateOf("") }
    var contentKor by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    var selectedImages by remember { mutableStateOf<List<ByteArray>>(emptyList()) }

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

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.widthIn(max = 1200.dp).fillMaxWidth().padding(16.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { launcher.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        Text("Click to upload images", color = Color.Gray, fontSize = 12.sp)
                        Text("JPEG, PNG, WEBP up to 2MB each. Max 10 images.", color = Color.Gray.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                }

                AnimatedVisibility(visible = selectedImages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Selected files: ${selectedImages.size} images ready.",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SecondaryButton(text = "Cancel", onClick = onDismiss, modifier = Modifier.weight(1f))
                    PrimaryButton(
                        text = "Create Announcement",
                        onClick = {
                            onConfirm(
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
}

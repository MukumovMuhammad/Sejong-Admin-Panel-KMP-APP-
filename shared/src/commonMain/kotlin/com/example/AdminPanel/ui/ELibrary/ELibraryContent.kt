package com.example.AdminPanel.ui.ELibrary

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.AdminPanel.PlatformStorageManager
import com.example.AdminPanel.data.model.Book
import com.example.AdminPanel.data.network.PdfDownloader
import com.example.AdminPanel.ui.components.*
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch

@Composable
fun ELibraryContent(viewModel: ELibraryViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var bookToDelete by remember { mutableStateOf<Book?>(null) }
    
    var searchText by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("All Genres") }

    val scope = rememberCoroutineScope()

    val storageManager = remember { PlatformStorageManager() }
    val downloader = remember { PdfDownloader(HttpClient(), storageManager) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dashboard > E-Library", color = Color.Gray, fontSize = 12.sp)
                    HeaderText("E-Library")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        text = "Export",
                        onClick = { /* Export logic */ },
                        modifier = Modifier.width(160.dp),
                        icon = Icons.Default.Share
                    )
                    PrimaryButton(
                        text = "Add New Book",
                        onClick = { showAddDialog = true },
                        modifier = Modifier.width(200.dp),
                        icon = Icons.Default.Add
                    )
                }
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard("Total Books", uiState.totalCount.toString(), "All books in library", Icons.Default.List, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Sejong Books", uiState.sejongCount.toString(), "Книги Sejong", Icons.Default.List, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Topik Books", uiState.topikCount.toString(), "Книги Topik", Icons.Default.List, Modifier.weight(1f), isLoading = uiState.isLoading)
                StatCard("Fiction Books", uiState.fictionCount.toString(), "Художественная литература", Icons.Default.List, Modifier.weight(1f), isLoading = uiState.isLoading)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Filters Bar
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
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
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search books by title, author...") },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )

                    GenreDropdown(
                        selectedGenre = selectedGenre,
                        onGenreSelected = { 
                            selectedGenre = it
                            viewModel.loadBooks(if (it == "All Genres") null else it)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { viewModel.loadBooks(if (selectedGenre == "All Genres") null else selectedGenre) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid
            val filteredBooks = uiState.books.filter { book ->
                val matchesSearch = searchText.isEmpty() ||
                        book.title_rus?.contains(searchText, ignoreCase = true) == true ||
                        book.title_taj?.contains(searchText, ignoreCase = true) == true ||
                        book.title_eng?.contains(searchText, ignoreCase = true) == true ||
                        book.author?.contains(searchText, ignoreCase = true) == true
                matchesSearch
            }

            if (uiState.isLoading && uiState.books.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredBooks.isEmpty()) {
                EmptyStateComponent(title = "No books found", icon = Icons.Default.Info)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 200.dp),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredBooks) { book ->
                        BookCard(
                            book = book,
                            onClick = { viewModel.selectBook(book) },
                            onDownloadClick = {
                                scope.launch {
                                    val targetUrl = book.file ?: return@launch

                                    // Format the string cleanly with no spaces and the correct .pdf extension
                                    val cleanFileName = "${book.author}_${book.title_rus}".replace(" ", "_") + ".pdf"

                                    // One method handles the network fetch AND opens the folder picker!
                                    val savedPath = downloader.downloadAndSaveWithDialog(targetUrl, cleanFileName)

                                    if (savedPath != null) {
                                        println("Saved successfully to: $savedPath")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Side Panels
        if (uiState.selectedBook != null) {
            AnimatedVisibility(
                visible = uiState.selectedBook != null,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Box(modifier = Modifier.width(500.dp).fillMaxHeight()) {
                    BookDetailsPanel(
                        book = uiState.selectedBook!!,
                        onClose = { viewModel.selectBook(null) },
                        onUpdate = { id, titles, descriptions, author, genre, date, file, cover ->
                            viewModel.updateBook(
                                id = id,
                                titleRus = titles[0], titleTaj = titles[1], titleEng = titles[2], titleKor = titles[3],
                                descriptionRus = descriptions[0], descriptionTaj = descriptions[1], descriptionEng = descriptions[2], descriptionKor = descriptions[3],
                                author = author, genres = genre, publishedDate = date, file = file, cover = cover
                            )
                        },
                        onDelete = { bookToDelete = it }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddBookSidePanel(
                onDismiss = { showAddDialog = false },
                onConfirm = { titles, descriptions, author, genre, date, file, cover ->
                    viewModel.createBook(
                        titles[0], titles[1], titles[2], titles[3],
                        descriptions[0], descriptions[1], descriptions[2], descriptions[3],
                        author, genre, date, file, cover
                    )
                    showAddDialog = false
                }
            )
        }

        // Delete Dialog
        if (bookToDelete != null) {
            AppDialog(
                title = "Delete Book?",
                message = "Are you sure you want to delete '${bookToDelete?.title_rus}'? This action cannot be undone.",
                onClose = { bookToDelete = null },
                onOkClick = {
                    bookToDelete?.id?.let { viewModel.deleteBook(it) }
                    bookToDelete = null
                },
                confirmText = "Delete",
                isDanger = true
            )
        }

        ActionStatusDialog(
            isLoading = uiState.isActionLoading,
            isSuccess = uiState.actionSuccess,
            error = uiState.error,
            onDismiss = { viewModel.resetActionState() }
        )
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit, onDownloadClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                AsyncImage(
                    model = book.cover,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Genre Badge
                Surface(
                    modifier = Modifier.padding(8.dp).align(Alignment.TopStart),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = book.genres ?: "",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                // File Type Badge
                Surface(
                    modifier = Modifier.padding(8.dp).align(Alignment.BottomEnd),
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = book.title_rus ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author ?: "Unknown Author",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = book.published_date ?: "", fontSize = 11.sp, color = Color.Gray)
                    IconButton(onClick = onDownloadClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDropdown(selectedGenre: String, onGenreSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    val genres = listOf("All Genres", "Книги Sejong", "Книги Topik", "Художественная литература")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedGenre,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = { Text(genre) },
                    onClick = {
                        onGenreSelected(genre)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun BookDetailsPanel(
    book: Book,
    onClose: () -> Unit,
    onUpdate: (String, List<String>, List<String>, String, String, String, ByteArray?, ByteArray?) -> Unit,
    onDelete: (Book) -> Unit
) {
    var titleRus by remember(book) { mutableStateOf(book.title_rus ?: "") }
    var titleTaj by remember(book) { mutableStateOf(book.title_taj ?: "") }
    var titleEng by remember(book) { mutableStateOf(book.title_eng ?: "") }
    var titleKor by remember(book) { mutableStateOf(book.title_kor ?: "") }

    var descRus by remember(book) { mutableStateOf(book.description_rus ?: "") }
    var descTaj by remember(book) { mutableStateOf(book.description_taj ?: "") }
    var descEng by remember(book) { mutableStateOf(book.description_eng ?: "") }
    var descKor by remember(book) { mutableStateOf(book.description_kor ?: "") }

    var author by remember(book) { mutableStateOf(book.author ?: "") }
    var genre by remember(book) { mutableStateOf(book.genres ?: "") }
    var publishedDate by remember(book) { mutableStateOf(book.published_date ?: "") }

    var selectedCover by remember { mutableStateOf<ByteArray?>(null) }
    var selectedFile by remember { mutableStateOf<ByteArray?>(null) }
    
    val scope = rememberCoroutineScope()

    val coverLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
        if (file != null) {
            scope.launch { selectedCover = file.readBytes() }
        }
    }
    val fileLauncher = rememberFilePickerLauncher(type = PickerType.File(listOf("pdf", "epub"))) { file ->
        if (file != null) {
            scope.launch { selectedFile = file.readBytes() }
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
                HeaderText("Book Details")
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = null) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Cover Preview
                Box(modifier = Modifier.size(120.dp, 180.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray).clickable { coverLauncher.launch() }) {
                    AsyncImage(
                        model = selectedCover ?: book.cover,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ID: ${book.id}", fontSize = 11.sp, color = Color.Gray)
                    AppTextField(value = author, onValueChange = { author = it }, label = "Author", placeholder = "Author name")
                    GenreDropdown(selectedGenre = genre, onGenreSelected = { genre = it }, modifier = Modifier.fillMaxWidth())
                    AppTextField(value = publishedDate, onValueChange = { publishedDate = it }, label = "Published Date", placeholder = "YYYY-MM-DD")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Titles", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            AppTextField(value = titleRus, onValueChange = { titleRus = it }, label = "Title (Rus)*", placeholder = "Russian title")
            AppTextField(value = titleTaj, onValueChange = { titleTaj = it }, label = "Title (Taj)", placeholder = "Tajik title")
            AppTextField(value = titleEng, onValueChange = { titleEng = it }, label = "Title (Eng)", placeholder = "English title")
            AppTextField(value = titleKor, onValueChange = { titleKor = it }, label = "Title (Kor)", placeholder = "Korean title")

            Spacer(modifier = Modifier.height(24.dp))

            Text("Descriptions", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            AppTextField(value = descRus, onValueChange = { descRus = it }, label = "Description (Rus)", placeholder = "Description in Russian", modifier = Modifier.fillMaxWidth(), singleLine = false)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { fileLauncher.launch() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (selectedFile != null) "New File Selected" else "Replace Book File (PDF/EPUB)")
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PrimaryButton(
                    text = "Save Changes",
                    onClick = {
                        onUpdate(
                            book.id!!,
                            listOf(titleRus, titleTaj, titleEng, titleKor),
                            listOf(descRus, descTaj, descEng, descKor),
                            author, genre, publishedDate, selectedFile, selectedCover
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { onDelete(book) },
                    modifier = Modifier.weight(0.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookSidePanel(
    onDismiss: () -> Unit,
    onConfirm: (List<String>, List<String>, String, String, String, ByteArray, ByteArray?) -> Unit
) {
    var titleRus by remember { mutableStateOf("") }
    var titleTaj by remember { mutableStateOf("") }
    var titleEng by remember { mutableStateOf("") }
    var titleKor by remember { mutableStateOf("") }

    var descRus by remember { mutableStateOf("") }
    var descTaj by remember { mutableStateOf("") }
    var descEng by remember { mutableStateOf("") }
    var descKor by remember { mutableStateOf("") }

    var author by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Книги Sejong") }
    var publishedDate by remember { mutableStateOf("") }

    var selectedCover by remember { mutableStateOf<ByteArray?>(null) }
    var selectedFile by remember { mutableStateOf<ByteArray?>(null) }
    
    val scope = rememberCoroutineScope()

    val coverLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
        if (file != null) {
            scope.launch { selectedCover = file.readBytes() }
        }
    }
    val fileLauncher = rememberFilePickerLauncher(type = PickerType.File(listOf("pdf", "epub"))) { file ->
        if (file != null) {
            scope.launch { selectedFile = file.readBytes() }
        }
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.widthIn(max = 1000.dp).fillMaxWidth().padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(32.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    HeaderText("Add New Book")
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Cover Upload
                    Box(
                        modifier = Modifier.size(120.dp, 180.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).clickable { coverLauncher.launch() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedCover != null) {
                            AsyncImage(model = selectedCover, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                                Text("Cover", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppTextField(value = titleRus, onValueChange = { titleRus = it }, label = "Title (Russian)*", placeholder = "Enter title")
                        AppTextField(value = author, onValueChange = { author = it }, label = "Author", placeholder = "Enter author")
                        GenreDropdown(selectedGenre = genre, onGenreSelected = { genre = it }, modifier = Modifier.fillMaxWidth())
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                     AppTextField(value = titleTaj, onValueChange = { titleTaj = it }, label = "Title (Tajik)", placeholder = "Optional", modifier = Modifier.weight(1f))
                     AppTextField(value = publishedDate, onValueChange = { publishedDate = it }, label = "Published Date", placeholder = "YYYY-MM-DD", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Descriptions", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                AppTextField(value = descRus, onValueChange = { descRus = it }, label = "Description (Russian)", placeholder = "Enter description", modifier = Modifier.fillMaxWidth(), singleLine = false)

                Spacer(modifier = Modifier.height(24.dp))
                
                // File Upload
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp).border(1.dp, if (selectedFile != null) MaterialTheme.colorScheme.primary else Color.LightGray, RoundedCornerShape(12.dp)).clickable { fileLauncher.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = if (selectedFile != null) MaterialTheme.colorScheme.primary else Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (selectedFile != null) "Book File Selected" else "Upload Book File (PDF/EPUB) *",
                            color = if (selectedFile != null) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SecondaryButton(text = "Cancel", onClick = onDismiss, modifier = Modifier.weight(1f))
                    PrimaryButton(
                        text = "Create Book",
                        onClick = {
                            selectedFile?.let {
                                onConfirm(
                                    listOf(titleRus, titleTaj, titleEng, titleKor),
                                    listOf(descRus, descTaj, descEng, descKor),
                                    author, genre, publishedDate, it, selectedCover
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = titleRus.isNotBlank() && selectedFile != null
                    )
                }
            }
        }
    }
}

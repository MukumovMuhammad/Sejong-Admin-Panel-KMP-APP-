package com.example.AdminPanel.ui.ELibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.api.BookApi
import com.example.AdminPanel.data.model.Book
import com.example.AdminPanel.data.network.HttpClientFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ELibraryUiState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val totalCount: Int = 0,
    val sejongCount: Int = 0,
    val topikCount: Int = 0,
    val fictionCount: Int = 0,
    val selectedBook: Book? = null,
    val isActionLoading: Boolean = false,
    val actionSuccess: Boolean = false
)

class ELibraryViewModel : ViewModel() {
    private val api = BookApi(HttpClientFactory.create())
    
    private val _uiState = MutableStateFlow(ELibraryUiState())
    val uiState: StateFlow<ELibraryUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks(genre: String? = null) {
        println("Loading book")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getBooks(genre)

                println("Got books!")
                println("Books: ${response}")
                
                val allBooks = response.books
                val sejong = allBooks.count { it.genres == "Книги Sejong" }
                val topik = allBooks.count { it.genres == "Книги Topik" }
                val fiction = allBooks.count { it.genres == "Художественная литература" }

                _uiState.value = _uiState.value.copy(
                    books = allBooks,
                    totalCount = response.total,
                    sejongCount = sejong,
                    topikCount = topik,
                    fictionCount = fiction,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load books"
                )
            }
        }
    }

    fun selectBook(book: Book?) {
        _uiState.value = _uiState.value.copy(selectedBook = book)
    }

    fun createBook(
        titleRus: String,
        titleTaj: String?,
        titleEng: String?,
        titleKor: String?,
        descriptionRus: String?,
        descriptionTaj: String?,
        descriptionEng: String?,
        descriptionKor: String?,
        author: String?,
        genres: String?,
        publishedDate: String?,
        file: ByteArray,
        cover: ByteArray?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
            try {
                val response = api.createBook(
                    titleRus = titleRus,
                    titleTaj = titleTaj,
                    titleEng = titleEng,
                    titleKor = titleKor,
                    descriptionRus = descriptionRus,
                    descriptionTaj = descriptionTaj,
                    descriptionEng = descriptionEng,
                    descriptionKor = descriptionKor,
                    author = author,
                    genres = genres,
                    publishedDate = publishedDate,
                    file = file,
                    cover = cover
                )
                if (response.error == null) {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true)
                    loadBooks()
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun updateBook(
        id: String,
        titleRus: String? = null,
        titleTaj: String? = null,
        titleEng: String? = null,
        titleKor: String? = null,
        descriptionRus: String? = null,
        descriptionTaj: String? = null,
        descriptionEng: String? = null,
        descriptionKor: String? = null,
        author: String? = null,
        genres: String? = null,
        publishedDate: String? = null,
        file: ByteArray? = null,
        cover: ByteArray? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
            try {
                val response = api.editBook(
                    id = id,
                    titleRus = titleRus,
                    titleTaj = titleTaj,
                    titleEng = titleEng,
                    titleKor = titleKor,
                    descriptionRus = descriptionRus,
                    descriptionTaj = descriptionTaj,
                    descriptionEng = descriptionEng,
                    descriptionKor = descriptionKor,
                    author = author,
                    genres = genres,
                    publishedDate = publishedDate,
                    file = file,
                    cover = cover
                )
                if (response.error == null) {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true)
                    loadBooks()
                    if (_uiState.value.selectedBook?.id == id) {
                        _uiState.value = _uiState.value.copy(selectedBook = response.book)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun deleteBook(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
            try {
                api.deleteBook(id)
                _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true)
                loadBooks()
                if (_uiState.value.selectedBook?.id == id) {
                    _uiState.value = _uiState.value.copy(selectedBook = null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }
    
    fun resetActionState() {
        _uiState.value = _uiState.value.copy(error = null, actionSuccess = false, isActionLoading = false)
    }
}

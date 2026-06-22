package com.example.AdminPanel.ui.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AdminPanel.data.api.AnnouncementApi
import com.example.AdminPanel.data.model.Announcement
import com.example.AdminPanel.data.network.HttpClientFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnnouncementsUiState(
    val announcements: List<Announcement> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val totalCount: Int = 0,
    val publishedCount: Int = 0,
    val draftsCount: Int = 0,
    val deletedCount: Int = 0
)

class AnnouncementsViewModel : ViewModel() {
    private val api = AnnouncementApi(HttpClientFactory.create())
    
    private val _uiState = MutableStateFlow(AnnouncementsUiState())
    val uiState: StateFlow<AnnouncementsUiState> = _uiState.asStateFlow()

    init {
        loadAnnouncements()
    }

    fun loadAnnouncements() {
        println("Fetching for the announcements")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getAnnouncements()
                println("Got announcements ${response}")
                // In a real app, these stats might come from the API
                _uiState.value = _uiState.value.copy(
                    announcements = response.announcements,
                    totalCount = response.total,
                    publishedCount = response.announcements.size, // Placeholder
                    draftsCount = 0, // Placeholder
                    deletedCount = 0, // Placeholder
                    isLoading = false
                )
            } catch (e: Exception) {
                print("Got errors fetching announcements: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load announcements"
                )
            }
        }
    }

    fun createAnnouncement(
        titleRus: String,
        titleTaj: String?,
        titleEng: String?,
        titleKor: String?,
        contentRus: String?,
        contentTaj: String?,
        contentEng: String?,
        contentKor: String?,
        images: List<ByteArray>? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, isSuccess = false)
            try {
                val response = api.createAnnouncement(
                    titleRus = titleRus,
                    titleTaj = titleTaj,
                    titleEng = titleEng,
                    titleKor = titleKor,
                    contentRus = contentRus,
                    contentTaj = contentTaj,
                    contentEng = contentEng,
                    contentKor = contentKor,
                    images = images
                )
                if (response.error == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                    loadAnnouncements()
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = response.error)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch {
            try {
                api.deleteAnnouncement(id)
                loadAnnouncements()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun resetState() {
        _uiState.value = _uiState.value.copy(error = null, isSuccess = false)
    }
}

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
    val deletedCount: Int = 0,
    val selectedAnnouncement: Announcement? = null,
    val isActionLoading: Boolean = false,
    val actionSuccess: Boolean = false
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

    fun selectAnnouncement(announcement: Announcement?) {
        _uiState.value = _uiState.value.copy(selectedAnnouncement = announcement)
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
            _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
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
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true)
                    loadAnnouncements()
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun updateAnnouncement(
        id: String,
        titleRus: String? = null,
        titleTaj: String? = null,
        titleEng: String? = null,
        titleKor: String? = null,
        contentRus: String? = null,
        contentTaj: String? = null,
        contentEng: String? = null,
        contentKor: String? = null,
        images: List<ByteArray>? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
            try {
                val response = api.editAnnouncement(
                    id = id,
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
                    _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true)
                    loadAnnouncements()
                    // Update selected announcement if it's the one being edited
                    if (_uiState.value.selectedAnnouncement?.id == id) {
                        _uiState.value = _uiState.value.copy(selectedAnnouncement = response.announcement)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isActionLoading = false, error = response.error)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, error = null, actionSuccess = false)
            try {
                api.deleteAnnouncement(id)
                _uiState.value = _uiState.value.copy(isActionLoading = false, actionSuccess = true)
                loadAnnouncements()
                if (_uiState.value.selectedAnnouncement?.id == id) {
                    _uiState.value = _uiState.value.copy(selectedAnnouncement = null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isActionLoading = false, error = e.message)
            }
        }
    }
    
    fun resetActionState() {
        _uiState.value = _uiState.value.copy(error = null, actionSuccess = false, isActionLoading = false)
    }

    fun resetState() {
        _uiState.value = _uiState.value.copy(error = null, isSuccess = false)
    }
}

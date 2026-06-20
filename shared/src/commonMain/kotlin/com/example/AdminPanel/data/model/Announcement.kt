package com.example.AdminPanel.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AnnouncementImage(
    val file_id: String,
    val url: String
)

@Serializable
data class Announcement(
    val id: String? = null,
    val title_taj: String? = null,
    val title_rus: String? = null,
    val title_eng: String? = null,
    val title_kor: String? = null,
    val content_taj: String? = null,
    val content_rus: String? = null,
    val content_eng: String? = null,
    val content_kor: String? = null,
    val images: List<AnnouncementImage> = emptyList(),
    val time_posted: String? = null,
    val author: String? = null
)

@Serializable
data class AnnouncementListResponse(
    val total: Int,
    val announcements: List<Announcement>
)

@Serializable
data class AnnouncementResponse(
    val message: String? = null,
    val announcement: Announcement? = null,
    val error: String? = null
)

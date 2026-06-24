package com.example.AdminPanel.data.model

import com.example.AdminPanel.data.utills.Filterable
import kotlinx.serialization.Serializable
import kotlin.time.Instant

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
): Filterable {
    override fun matchesSearch(query: String) =
        title_rus?.contains(query, true) == true || title_taj?.contains(query, true) == true

    override fun recordTimestamp() = try {
        // Your Instant.parse conversion logic safely handled inside the model container!
        time_posted?.let { Instant.parse(it.trim().replace(" ", "T")).toEpochMilliseconds() }
    } catch(e: Exception) { null }
}

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

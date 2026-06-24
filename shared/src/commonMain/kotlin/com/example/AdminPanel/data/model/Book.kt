package com.example.AdminPanel.data.model

import com.example.AdminPanel.data.utills.Filterable
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Book(
    val id: String? = null,
    val title_taj: String? = null,
    val title_rus: String? = null,
    val title_eng: String? = null,
    val title_kor: String? = null,
    val description_taj: String? = null,
    val description_rus: String? = null,
    val description_eng: String? = null,
    val description_kor: String? = null,
    val author: String? = null,
    val genres: String? = null,
    val published_date: String? = null,
    val created_at: String? = null,
    val cover: String? = null,
    val cover_id: String? = null,
    val file: String? = null,
    val file_id: String? = null
): Filterable{
    override fun matchesSearch(query: String): Boolean = title_rus?.contains(query, true) == true || title_taj?.contains(query, true) == true

    override fun primaryCategory(): String? = genres

    override fun recordTimestamp(): Long? = try {
        // Your Instant.parse conversion logic safely handled inside the model container!
        published_date?.let { Instant.parse(it.trim().replace(" ", "T")).toEpochMilliseconds() }
    } catch(e: Exception) { null }



}

@Serializable
data class BookListResponse(
    val total: Int,
    val books: List<Book>
)

@Serializable
data class BookResponse(
    val message: String? = null,
    val book: Book? = null,
    val error: String? = null
)

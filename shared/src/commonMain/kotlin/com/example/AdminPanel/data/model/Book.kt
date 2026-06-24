package com.example.AdminPanel.data.model

import kotlinx.serialization.Serializable

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
)

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

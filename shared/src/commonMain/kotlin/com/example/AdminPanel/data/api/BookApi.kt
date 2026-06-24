package com.example.AdminPanel.data.api

import com.example.AdminPanel.data.model.BookListResponse
import com.example.AdminPanel.data.model.BookResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class BookApi(private val client: HttpClient) {

    suspend fun getBooks(genre: String? = null): BookListResponse {
        return client.get("books/") {
            genre?.let { parameter("genres", it) }
        }.body()
    }

    suspend fun getBook(id: String): BookResponse {
        return client.get("books/$id/").body()
    }

    suspend fun createBook(
        titleRus: String,
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
        file: ByteArray,
        cover: ByteArray? = null
    ): BookResponse {
        return client.submitFormWithBinaryData(
            url = "books/admin/create/",
            formData = formData {
                append("title_rus", titleRus)
                titleTaj?.let { append("title_taj", it) }
                titleEng?.let { append("title_eng", it) }
                titleKor?.let { append("title_kor", it) }
                descriptionRus?.let { append("description_rus", it) }
                descriptionTaj?.let { append("description_taj", it) }
                descriptionEng?.let { append("description_eng", it) }
                descriptionKor?.let { append("description_kor", it) }
                author?.let { append("author", it) }
                genres?.let { append("genres", it) }
                publishedDate?.let { append("published_date", it) }

                append("file", file, Headers.build {
                    append(HttpHeaders.ContentType, "application/pdf")
                    append(HttpHeaders.ContentDisposition, "filename=\"book.pdf\"")
                })

                cover?.let {
                    append("cover", it, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"cover.jpg\"")
                    })
                }
            }
        ).body()
    }

    suspend fun editBook(
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
    ): BookResponse {
        return client.patch("books/admin/$id/edit/") {
            setBody(MultiPartFormDataContent(
                formData {
                    titleRus?.let { append("title_rus", it) }
                    titleTaj?.let { append("title_taj", it) }
                    titleEng?.let { append("title_eng", it) }
                    titleKor?.let { append("title_kor", it) }
                    descriptionRus?.let { append("description_rus", it) }
                    descriptionTaj?.let { append("description_taj", it) }
                    descriptionEng?.let { append("description_eng", it) }
                    descriptionKor?.let { append("description_kor", it) }
                    author?.let { append("author", it) }
                    genres?.let { append("genres", it) }
                    publishedDate?.let { append("published_date", it) }

                    file?.let {
                        append("file", it, Headers.build {
                            append(HttpHeaders.ContentType, "application/pdf")
                            append(HttpHeaders.ContentDisposition, "filename=\"book.pdf\"")
                        })
                    }

                    cover?.let {
                        append("cover", it, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"cover.jpg\"")
                        })
                    }
                }
            ))
        }.body()
    }

    suspend fun deleteBook(id: String): BookResponse {
        return client.delete("books/admin/$id/delete/").body()
    }
}

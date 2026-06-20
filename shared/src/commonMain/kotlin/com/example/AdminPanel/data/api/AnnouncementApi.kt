package com.example.AdminPanel.data.api

import com.example.AdminPanel.data.model.AnnouncementListResponse
import com.example.AdminPanel.data.model.AnnouncementResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class AnnouncementApi(private val client: HttpClient) {

    suspend fun getAnnouncements(): AnnouncementListResponse {
        return client.get("announcements/").body()
    }

    suspend fun createAnnouncement(
        titleRus: String,
        titleTaj: String? = null,
        titleEng: String? = null,
        titleKor: String? = null,
        contentRus: String? = null,
        contentTaj: String? = null,
        contentEng: String? = null,
        contentKor: String? = null,
        images: List<ByteArray>? = null
    ): AnnouncementResponse {
        return client.submitFormWithBinaryData(
            url = "announcements/admin/create/",
            formData = formData {
                append("title_rus", titleRus)
                titleTaj?.let { append("title_taj", it) }
                titleEng?.let { append("title_eng", it) }
                titleKor?.let { append("title_kor", it) }
                contentRus?.let { append("content_rus", it) }
                contentTaj?.let { append("content_taj", it) }
                contentEng?.let { append("content_eng", it) }
                contentKor?.let { append("content_kor", it) }
                
                images?.forEachIndexed { index, bytes ->
                    append("images", bytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"image_$index.jpg\"")
                    })
                }
            }
        ).body()
    }

    suspend fun editAnnouncement(
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
    ): AnnouncementResponse {
        return client.patch("announcements/admin/$id/edit/") {
            setBody(MultiPartFormDataContent(
                formData {
                    titleRus?.let { append("title_rus", it) }
                    titleTaj?.let { append("title_taj", it) }
                    titleEng?.let { append("title_eng", it) }
                    titleKor?.let { append("title_kor", it) }
                    contentRus?.let { append("content_rus", it) }
                    contentTaj?.let { append("content_taj", it) }
                    contentEng?.let { append("content_eng", it) }
                    contentKor?.let { append("content_kor", it) }
                    
                    images?.forEachIndexed { index, bytes ->
                        append("images", bytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"image_$index.jpg\"")
                        })
                    }
                }
            ))
        }.body()
    }

    suspend fun deleteAnnouncement(id: String): AnnouncementResponse {
        return client.delete("announcements/admin/$id/delete/").body()
    }
}

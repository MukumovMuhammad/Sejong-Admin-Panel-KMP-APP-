package com.example.AdminPanel.data.api

import com.example.AdminPanel.data.model.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.HttpResponse

class AuthApi(
    private val client: HttpClient
) {
    suspend fun login(
        username: String,
        password: String,
        device_token: String
    ): HttpResponse {
        return client.post("users/login/") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username, password, device_token))
        }
    }

    

    suspend fun logout(): HttpResponse {
        return client.post("logout/")
    }
}

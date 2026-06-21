package com.example.AdminPanel.data.network

import com.example.AdminPanel.data.session.SessionManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {

    fun create() = HttpClient(CIO) {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true

                prettyPrint = true
            })

        }

        defaultRequest {
            url("https://sejong-app-container-847502443673.us-central1.run.app/api/")
            SessionManager.token?.let {
                header("Authorization", "Bearer $it")
            }
        }
    }
}

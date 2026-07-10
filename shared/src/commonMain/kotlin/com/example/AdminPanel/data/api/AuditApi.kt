package com.example.AdminPanel.data.api

import com.example.AdminPanel.data.model.AuditLogResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuditApi(private val client: HttpClient) {

    suspend fun getAuditLogs(
        action: String? = null,
        adminUser: String? = null,
        modelName: String? = null,
        limit: Int = 50,
        offset: Int = 0
    ): AuditLogResponse {
        return client.get("audit/admin/logs/") {
            action?.let { parameter("action", it) }
            adminUser?.let { parameter("admin_user", it) }
            modelName?.let { parameter("model_name", it) }
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }
}

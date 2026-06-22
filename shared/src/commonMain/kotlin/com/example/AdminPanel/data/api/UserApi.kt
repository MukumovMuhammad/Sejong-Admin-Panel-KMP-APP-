package com.example.AdminPanel.data.api

import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.data.model.UserListResponse
import com.example.AdminPanel.data.model.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class UserApi(private val client: HttpClient) {

    suspend fun getUsers(
        status: String? = null,
        verificationStatus: String? = null,
        groupId: String? = null
    ): UserListResponse {
        return client.get("users/admin/users/") {
            status?.let { parameter("status", it) }
            verificationStatus?.let { parameter("verification_status", it) }
            groupId?.let { parameter("group_id", it) }
        }.body()
    }

    suspend fun getUser(userId: String): UserResponse {
        val cleanId = userId.removePrefix("users/")
        return client.get("users/admin/users/$cleanId/").body()
    }

    suspend fun createUser(user: User): UserResponse {
        return client.post("users/admin/users/create/") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }

    suspend fun editUser(userId: String, updates: Map<String, String?>): UserResponse {
        val cleanId = userId.removePrefix("users/")
        return client.patch("users/admin/users/$cleanId/edit/") {
            contentType(ContentType.Application.Json)
            setBody(updates)
        }.body()
    }

    // Assuming delete exists based on UI
    suspend fun deleteUser(userId: String): UserResponse {
        val cleanId = userId.removePrefix("users/")
        return client.delete("users/admin/users/$cleanId/").body()
    }
}

package com.example.AdminPanel.data.api

import com.example.AdminPanel.data.model.User
import com.example.AdminPanel.data.model.UserListResponse
import com.example.AdminPanel.data.model.UserResponse
import com.example.AdminPanel.data.model.simpleMessageResponse
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

    suspend fun verifyUser(userId: String, action: String): UserResponse {
        val cleanId = userId.removePrefix("users/")
        return client.post("users/admin/verify/$cleanId/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("action" to action))
        }.body()
    }

    suspend fun emailVerify(email: String, code: String): simpleMessageResponse {
        return client.post("users/register/verify/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email, "code" to code))
        }.body()
    }

    suspend fun resendVerificationCode(email: String): simpleMessageResponse {
        return client.post("users/register/resend-code/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email))
        }.body()
    }

    suspend fun setStatus(userId: String, status: String): UserResponse {
        val cleanId = userId.removePrefix("users/")
        return client.post("users/admin/set-status/$cleanId/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }.body()
    }

    // Assuming delete exists based on UI
    suspend fun deleteUser(userId: String): UserResponse {
        val cleanId = userId.removePrefix("users/")
        return client.delete("users/admin/users/$cleanId/").body()
    }
}

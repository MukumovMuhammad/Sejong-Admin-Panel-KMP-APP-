package com.example.AdminPanel.data.api

import com.example.AdminPanel.data.model.Group
import com.example.AdminPanel.data.model.GroupListResponse
import com.example.AdminPanel.data.model.GroupResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class GroupApi(private val client: HttpClient) {

    suspend fun getGroups(): GroupListResponse {
        return client.get("groups/admin/").body()
    }

    suspend fun createGroup(name: String): GroupResponse {
        return client.post("groups/admin/create/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name))
        }.body()
    }

    suspend fun deleteGroup(groupId: String): GroupResponse {
        val cleanId = groupId.removePrefix("groups/")
        return client.delete("groups/admin/$cleanId/delete/").body()
    }

    suspend fun assignUserToGroup(userId: String, groupId: String): GroupResponse {
        val cleanUserId = userId.removePrefix("users/")
        return client.post("groups/admin/assign/$cleanUserId/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("group_id" to groupId))
        }.body()
    }
}

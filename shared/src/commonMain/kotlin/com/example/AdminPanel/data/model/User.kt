package com.example.AdminPanel.data.model

import com.example.AdminPanel.data.utills.Filterable
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val fullname: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val status: String,
    val verification_status: String,
    val group: String? = "",
    val group_id: String? = null,
    val avatar: String? = null,
    val date_joined: String? = null,
    val date_of_birth: String? = null,
    val device_token: String? = null
): Filterable{
    override fun matchesSearch(query: String) =
        fullname?.contains(query, true) == true || username.contains(query, true) || email?.contains(query, true) == true

    

    override fun primaryCategory() = status
    override fun secondaryCategory() = verification_status
    override fun group(): String? = group
}

@Serializable
data class UserListResponse(
    val total: Int = 0,
    val users: List<User> = emptyList()
)

@Serializable
data class UserResponse(
    val user: User? = null,
    val message: String? = null,
    val error: String? = null
)

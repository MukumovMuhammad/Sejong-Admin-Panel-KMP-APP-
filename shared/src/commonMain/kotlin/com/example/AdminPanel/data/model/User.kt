package com.example.AdminPanel.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val fullname: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val date_of_birth: String? = null,
    val status: String, // Guest, Student, Teacher, Admin
    val verification_status: String, // Pending, Approved, Rejected
    val group: String? = "",
    val avatar: String? = null,
    val date_joined: String? = null
)

@Serializable
data class UserListResponse(
    val total: Int,
    val users: List<User>
)

@Serializable
data class UserResponse(
    val user: User? = null,
    val message: String? = null,
    val error: String? = null
)

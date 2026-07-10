package com.example.AdminPanel.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String? = null,
    val name: String,
    // Fields for UI (some might be mocked if API doesn't provide them yet)
    val level: String? = "Beginner",
    val teacher_name: String? = null,
    val teacher_email: String? = null,
    val teacher_avatar: String? = null,
    val students_count: Int = 0,
    val max_students: Int = 30,
    val schedule: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val location: String? = null,
    val status: String? = "Active", // Active, Upcoming
    val description: String? = null
)

@Serializable
data class GroupListResponse(
    val total: Int = 0,
    val has_more: Boolean = false,
    val groups: List<Group> = emptyList()
)

@Serializable
data class GroupResponse(
    val message: String? = null,
    val group: Group? = null,
    val error: String? = null
)

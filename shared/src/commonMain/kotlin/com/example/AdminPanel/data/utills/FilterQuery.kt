package com.example.AdminPanel.data.utills

data class FilterQuery(
    val search: String = "",
    val category: String = "",       // Maps to "status", "group", or "type" depending on screen
    val subCategory: String = "",    // Maps to "verification_status" etc.
    val startDate: Long? = null,
    val endDate: Long? = null
)
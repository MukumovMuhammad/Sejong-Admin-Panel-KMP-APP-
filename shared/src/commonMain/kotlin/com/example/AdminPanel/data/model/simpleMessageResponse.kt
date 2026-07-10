package com.example.AdminPanel.data.model

import kotlinx.serialization.Serializable

@Serializable
data class simpleMessageResponse(
    val message: String? = null,
    val error: String? = null,
    val avatar: String? = null
)

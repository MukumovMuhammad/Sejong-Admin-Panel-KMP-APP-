package com.example.AdminPanel.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(

    val username: String,
    val password: String,
    @SerialName("device_token")
    val deviceToken: String? = null
)

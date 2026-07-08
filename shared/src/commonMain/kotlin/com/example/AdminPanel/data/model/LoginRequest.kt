package com.example.AdminPanel.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(

    val username: String,
    val password: String,
    val device_token: String
)

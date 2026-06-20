package com.example.AdminPanel.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(

    val message: String,

    val token: String,

    val status: String,

    @SerialName("verification_status")
    val verificationStatus: String
)

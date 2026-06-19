package com.example.AdminPanel

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
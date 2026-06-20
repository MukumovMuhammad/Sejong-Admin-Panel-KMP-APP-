package com.example.AdminPanel.data.session

import com.russhwolf.settings.Settings

object SessionManager {
    private val settings = Settings()

    // 1. Remember Me State (Saves if the user checked the box)
    var rememberMe: Boolean
        get() = settings.getBoolean("remember_me", false)
        set(value) = settings.putBoolean("remember_me", value)

    // 2. Login Status State
    var isLoggedIn: Boolean
        get() = settings.getBoolean("is_logged_in", false)
        set(value) = settings.putBoolean("is_logged_in", value)

    // 3. Token String (Uses getter/setter so it reads directly from disk)
    var token: String?
        get() = settings.getStringOrNull("user_token")
        set(value) {
            if (value != null) settings.putString("user_token", value)
            else settings.remove("user_token")
        }

    // 4. Status String
    var status: String?
        get() = settings.getStringOrNull("user_status")
        set(value) {
            if (value != null) settings.putString("user_status", value)
            else settings.remove("user_status")
        }

    // 5. Verification Status String
    var verificationStatus: String?
        get() = settings.getStringOrNull("verify_status")
        set(value) {
            if (value != null) settings.putString("verify_status", value)
            else settings.remove("verify_status")
        }

    // 6. Clear everything on logout
    fun clearSession() {
        settings.clear()
    }
}

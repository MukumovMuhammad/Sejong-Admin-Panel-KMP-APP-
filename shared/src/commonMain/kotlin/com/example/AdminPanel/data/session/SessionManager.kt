package com.example.AdminPanel.data.session



import androidx.compose.ui.graphics.Paint
import com.russhwolf.settings.Settings

object SessionManager {
    private val settings = Settings()
    var token: String? = null
    var status: String? = null
    var verificationStatus: String? = null

    fun setTokenStatus( token: String, status: String){
        settings.putString("token", token)
        settings.putString("status", status)
        settings.putBoolean("is_logged_in", true)
    }


    // Example: Load login state
    fun isLoggedIn(): Boolean {
        return settings.getBoolean("is_logged_in", false)
    }

    fun getTokenAndStatus(): Pair<String, String>{
        return Pair(
            settings.getString("token", "null"),
            settings.getString("status", "null")
        )
    }


    fun clear() {
        token = null
        status = null
        verificationStatus = null
    }
    fun clearSession() {
        settings.clear()
    }
}

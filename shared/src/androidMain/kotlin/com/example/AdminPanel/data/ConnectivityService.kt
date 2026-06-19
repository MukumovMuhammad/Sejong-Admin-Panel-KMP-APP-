package com.example.AdminPanel.data

// Simple implementation for Android, for now just returning true 
// or using a similar approach to JVM if possible without Context for now.
// Real Android implementation usually requires ConnectivityManager and Context.
class AndroidConnectivityService : ConnectivityService {
    override fun isConnected(): Boolean {
        // Mocked for now to avoid dependency on Context in shared module without injection
        return true 
    }
}

actual fun getConnectivityService(): ConnectivityService = AndroidConnectivityService()

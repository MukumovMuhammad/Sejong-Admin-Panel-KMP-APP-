package com.example.AdminPanel.data

import java.net.InetAddress

class JvmConnectivityService : ConnectivityService {
    override fun isConnected(): Boolean {
        return try {
            val address = InetAddress.getByName("8.8.8.8")
            address.isReachable(2000)
        } catch (e: Exception) {
            false
        }
    }
}

actual fun getConnectivityService(): ConnectivityService = JvmConnectivityService()

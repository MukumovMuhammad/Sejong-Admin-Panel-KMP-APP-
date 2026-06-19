package com.example.AdminPanel.data

interface ConnectivityService {
    fun isConnected(): Boolean
}

expect fun getConnectivityService(): ConnectivityService

package com.example.AdminPanel.data

import kotlinx.browser.window

class JsConnectivityService : ConnectivityService {
    override fun isConnected(): Boolean {
        return window.navigator.onLine
    }
}

actual fun getConnectivityService(): ConnectivityService = JsConnectivityService()

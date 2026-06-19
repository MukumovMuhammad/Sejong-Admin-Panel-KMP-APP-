package com.example.AdminPanel.data

// window.navigator.onLine should work in Wasm too if using similar JS interop
// For now, simple mock
class WasmConnectivityService : ConnectivityService {
    override fun isConnected(): Boolean {
        return true
    }
}

actual fun getConnectivityService(): ConnectivityService = WasmConnectivityService()

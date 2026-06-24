package com.example.AdminPanel

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


// commonMain
expect class PlatformStorageManager() {
    suspend fun savePdf(fileName: String, bytes: ByteArray): String?
    suspend fun savePdfWithDialog(suggestedName: String, bytes: ByteArray): String?
}
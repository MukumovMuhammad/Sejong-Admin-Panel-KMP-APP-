package com.example.AdminPanel.data.network


import com.example.AdminPanel.PlatformStorageManager
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

// commonMain/src/commonMain/kotlin/PdfDownloader.kt

class PdfDownloader(
    private val httpClient: HttpClient,
    private val storageManager: PlatformStorageManager
) {
    suspend fun downloadAndSavePdf(url: String?, fileName: String): String? {
        if (url.isNullOrEmpty()){
            return null
        }
        return try {
            // Fetch raw response bytes via Ktor
            val response: HttpResponse = httpClient.get(url)
            val bytes = response.readBytes()

            // Hand off bytes to the respective platform manager
            storageManager.savePdf(fileName, bytes)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun downloadAndSaveWithDialog(url: String, suggestedName: String): String? {
        return try {
            // 1. Fetch bytes directly from network stream
            val responseBytes = httpClient.get(url).readBytes()

            // 2. Hand off instantly to native dialog code
            storageManager.savePdfWithDialog(suggestedName, responseBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun downloadXlsxAndSaveWithDialog(url: String, suggestedName: String): String? {
        println("Downloading $url")
        return try {
            // 1. Fetch bytes directly from network stream
            val responseBytes = httpClient.get(url).readBytes()

            // 2. Hand off instantly to native dialog code

            storageManager.saveXlsxWithDialog(suggestedName, responseBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveXlsxWithDialog(suggestedName: String, bytes: ByteArray): String? {
        return storageManager.saveXlsxWithDialog(suggestedName, bytes)
    }
}
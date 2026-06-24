package com.example.AdminPanel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Paths
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual class PlatformStorageManager actual constructor() {
    actual suspend fun savePdf(fileName: String, bytes: ByteArray): String? {
        return try {
            // 1. Automatically locates the user's standard home directory (e.g., C:\Users\Name or /Users/Name)
            val userHome = System.getProperty("user.home")

            // 2. Targets the "Downloads" folder inside their home directory
            val downloadsDir = File(userHome, "Downloads")

            // Create the folder if it doesn't exist for some reason
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            // 3. Write out the raw payload bytes
            val targetFile = File(downloadsDir, fileName)
            targetFile.writeBytes(bytes)

            // Return the absolute file path so your UI can display where it went!
            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual suspend fun savePdfWithDialog(suggestedName: String, bytes: ByteArray): String? = withContext(
            Dispatchers.IO) {
            try {
                val fileChooser = JFileChooser().apply {
                    dialogTitle = "Save PDF File"
                    selectedFile = File(suggestedName) // Default name suggested to the user
                    fileFilter = FileNameExtensionFilter("PDF Documents", "pdf")
                }

                // Opens the native Desktop "Save As" window
                val userSelection = fileChooser.showSaveDialog(null)

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    var selectedFile = fileChooser.selectedFile

                    // Automatically append .pdf extension if the user forgot to type it
                    if (!selectedFile.name.lowercase().endsWith(".pdf")) {
                        selectedFile = File(selectedFile.absolutePath + ".pdf")
                    }

                    selectedFile.writeBytes(bytes)
                    selectedFile.absolutePath
                } else {
                    null // User cancelled the dialog
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}
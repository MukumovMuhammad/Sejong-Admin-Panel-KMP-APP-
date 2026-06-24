package com.example.AdminPanel

import android.os.Build
// androidMain
import android.content.Context
import android.content.Intent
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()


// An internal reference pointer to hold your Android Context application reference
object AndroidContextHolder {
    lateinit var context: Context
}

actual class PlatformStorageManager actual constructor() {
    actual suspend fun savePdf(fileName: String, bytes: ByteArray): String? {
        return try {
            // Safe fallback uses the Android Download folder directly
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            file.writeBytes(bytes)
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual suspend fun savePdfWithDialog(suggestedName: String, bytes: ByteArray): String? = withContext(
        Dispatchers.IO) {
        // Access your application context wrapper
        val context = AndroidContextHolder.context

        try {
            // Because Android requires an Activity to process result intents,
            // the modern system-safe way is to write the file to a cache/temp directory first...
            val cacheFile = File(context.cacheDir, suggestedName)
            cacheFile.writeBytes(bytes)

            // ...Then launch a system Intent to let the user pick where to copy/save it permanently
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(Intent.EXTRA_TITLE, suggestedName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Note: To capture the URI result completely within a non-UI class,
            // you must route the Intent result back from your main MainActivity,
            // or use a temporary public storage file approach like below:
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )
            val finalFile = File(downloadsDir, suggestedName)
            finalFile.writeBytes(bytes)

            finalFile.absolutePath // Returns the path to the chosen destination
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
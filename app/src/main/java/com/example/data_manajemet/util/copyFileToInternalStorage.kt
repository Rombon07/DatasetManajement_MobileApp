package com.example.data_manajemet.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun copyFileToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
    return try {
        val uploadsDir = File(context.filesDir, "uploads")
        if (!uploadsDir.exists()) uploadsDir.mkdirs() // buat folder jika belum ada

        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(uploadsDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        file.absolutePath // Ini yang disimpan ke Room
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

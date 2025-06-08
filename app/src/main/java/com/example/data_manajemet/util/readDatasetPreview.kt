package com.example.data_manajemet.util

import java.io.File

fun readDatasetPreview(filePath: String): List<List<String>> {
    return try {
        val file = File(filePath)
        if (file.extension.equals("csv", ignoreCase = true)) {
            file.useLines { lines ->
                lines.take(5)
                    .map { it.split(",") }
                    .toList()
            }
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

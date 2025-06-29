package com.example.data_manajemet.network.response

data class DatasetResponse(
    val id: Int,
    val name: String,
    val description: String,
    val status: String,
    val cover_image: String?,
    val data_file: String?,
    val uploaded_at: String,
    val owner: Int,
    val request_note: String?
)

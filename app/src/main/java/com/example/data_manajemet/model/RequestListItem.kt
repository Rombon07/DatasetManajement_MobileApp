package com.example.data_manajemet.model

data class RequestListItem(
    val id: Int,
    val nama_model: String,
    val kebutuhan: String,
    val deskripsi: String,
    val timestamp: String,
    val dataset: DatasetItem?
)

data class DatasetItem(
    val id: Int,
    val name: String,
    val description: String,
    val cover_image: String,
    val data_file: String,
    val uploaded_at: String,
    val status: String,
    val owner: Int
)

data class DatasetUploadRequest(
    val name: String,
    val description: String,
    val status: String,
    val owner: Int // atau hapus kalau diambil dari token user di backend
)

sealed class CombinedItem {
    data class Request(val item: RequestListItem) : CombinedItem()
    data class Dataset(val item: DatasetItem) : CombinedItem()
}
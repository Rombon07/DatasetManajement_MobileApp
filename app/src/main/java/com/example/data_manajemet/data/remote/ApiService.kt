package com.example.data_manajemet.data.remote

import retrofit2.Response
import retrofit2.http.GET

data class DatasetRequestResponse(
    val success: Boolean,
    val data: List<DatasetRequestItem>
)

data class DatasetRequestItem(
    val id: Int,
    val nama_model: String,
    val kebutuhan: String,
    val deskripsi: String,
    val timestamp: String,
    val dataset: DatasetItem? // bisa null
)

data class DatasetItem(
    val id: Int,
    val name: String,
    val description: String,
    val status: String,
    val data_file: String?
)

interface ApiService {
    @GET("/request-list/")
    suspend fun getDatasetRequests(): Response<DatasetRequestResponse>
}

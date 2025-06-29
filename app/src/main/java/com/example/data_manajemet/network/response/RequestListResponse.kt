package com.example.data_manajemet.network.response
import com.example.data_manajemet.model.DatasetItem
import com.example.data_manajemet.model.RequestListItem

data class RequestListResponse(
    val success: Boolean,
    val data: List<RequestListItem>
)


data class RequestListItem(
    val id: Int,
    val nama_model: String,
    val kebutuhan: String,
    val deskripsi: String,
    val timestamp: String,
    val dataset: DatasetItem?
)

//data class RequestItem(
//    val id: Int,
//    val nama_model: String,
//    val kebutuhan: String,
//    val deskripsi: String,
//    val timestamp: String,
//    val dataset: DatasetInfo
//)

data class DatasetInfo(
    val id: Int,
    val name: String,
    val description: String,
    val status: String,
    val data_file: String
)


package com.example.data_manajemet.repository

import com.example.data_manajemet.model.RequestListItem
import com.example.data_manajemet.network.RetrofitInstance
import okhttp3.ResponseBody
import retrofit2.Response

class RequestRepository {
    suspend fun getRequestList(): List<RequestListItem> {
        val response = RetrofitInstance.api.getRequestList()
        if (response.isSuccessful) {
            return response.body()?.data ?: emptyList()
        } else {
            throw Exception("Gagal load data: ${response.code()}")
        }
    }

    // Fungsi konfirmasi penggabungan dataset dengan request
    suspend fun konfirmasiPilihDataset(requestId: Int, datasetId: Int): Response<ResponseBody> {
        return RetrofitInstance.api.konfirmasiPilihDataset(requestId, datasetId)
    }
}

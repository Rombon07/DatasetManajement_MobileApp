package com.example.data_manajemet.network

import com.example.data_manajemet.model.DatasetUploadRequest
import com.example.data_manajemet.network.response.DatasetResponse
import com.example.data_manajemet.network.response.RequestListResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DatasetApiService {
    @GET("api/request-list/")
    suspend fun getRequestList(): Response<RequestListResponse>

    @GET("api/datasets/")
    suspend fun getDatasets(): List<DatasetResponse>

    @POST("api/datasets/")
    suspend fun uploadDataset(
        @Body datasetUploadRequest: DatasetUploadRequest
    ): DatasetResponse


    @POST("api/konfirmasi-pilih-dataset/{request_id}/{dataset_id}/")
    suspend fun konfirmasiPilihDataset(
        @Path("request_id") requestId: Int,
        @Path("dataset_id") datasetId: Int
    ): Response<ResponseBody>


}


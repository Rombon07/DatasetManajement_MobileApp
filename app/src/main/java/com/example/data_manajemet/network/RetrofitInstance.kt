package com.example.data_manajemet.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: DatasetApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://172.16.59.234:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatasetApiService::class.java)
    }
}

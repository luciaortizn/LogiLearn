package com.example.logilearnapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DeeplApiClient {
    private const val BASE_URL = "https://api-free.deepl.com/v2/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val deepLService: DeeplApiService.DeepLApiService = retrofit.create(DeeplApiService.DeepLApiService::class.java)
}
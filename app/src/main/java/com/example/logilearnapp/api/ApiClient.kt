package com.example.logilearnapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    //definir como objeto esta clase implica que tiene ámbito de sigleton
    //instancia única en toda la aplicación
    private const val BASE_URL = "http://api.wordnik.com/v4/"

    fun createService(apiKey: String): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
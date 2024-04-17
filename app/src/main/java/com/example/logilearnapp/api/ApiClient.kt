package com.example.logilearnapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    //definir como objeto esta clase implica que tiene ámbito de sigleton
    //instancia única en toda la aplicación
    private const val BASE_URL = "https://api.wordnik.com/v4/"

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val wordnikService: ApiService = retrofit.create(ApiService::class.java)

}
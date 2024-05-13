package com.example.logilearnapp.api

import com.example.logilearnapp.data.TranslateRequest
import com.example.logilearnapp.data.TranslateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeeplApiService {
    interface DeepLApiService {
        @POST("translate")
        fun translate(
            @Header("Authorization") authorization: String,
            @Header("Content-Type") contentType: String,
            @Body requestBody: TranslateRequest
        ): Call<TranslateResponse>
    }
}
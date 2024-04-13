package com.example.logilearnapp.api
import com.example.logilearnapp.data.WordOfTheDayResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
interface ApiService {
    //esta interfaz tiene métodos para cada llamada
    @GET("word.json/wordOfTheDay")
    fun getWordOfTheDay(@Query("api_key") apiKey: String): Call<WordOfTheDayResponse>
}
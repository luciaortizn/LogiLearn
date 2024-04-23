package com.example.logilearnapp.api
import com.example.logilearnapp.data.Definition
import com.example.logilearnapp.data.Examples
import com.example.logilearnapp.data.Pronunciation
import com.example.logilearnapp.data.RelatedWord
import com.example.logilearnapp.data.WordOfTheDayResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Path

interface ApiService {
    //esta interfaz tiene métodos para cada llamada

    @GET("words.json/wordOfTheDay?")
    fun getWordOfTheDay(@Query("api_key") apiKey: String): Call<WordOfTheDayResponse>

    @GET("word.json/{word}/definitions?")
    fun getDefinitions(@Path("word") word: String, @Query("api_key") apiKey: String): Call<List<Definition>>

    @GET("word.json/{word}/relatedWords?")
    fun getRelatedWords(@Path("word") word: String, @Query("api_key")apiKey : String): Call <List<RelatedWord>>

    @GET("word.json/{word}/examples?")
    fun getExamples(@Path("word") word: String, @Query("api_key")apiKey : String): Call<Examples>

    @GET("word.json/{word}/pronunciations?")
    fun getPronunciation(@Path("word") word: String, @Query("api_key")apiKey : String): Call <List<Pronunciation>>





}
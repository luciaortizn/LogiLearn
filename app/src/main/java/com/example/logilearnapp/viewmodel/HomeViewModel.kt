package com.example.logilearnapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.logilearnapp.api.ApiClient
import com.example.logilearnapp.data.Definition
import com.example.logilearnapp.data.Example
import com.example.logilearnapp.data.Examples
import com.example.logilearnapp.data.Pronunciation
import com.example.logilearnapp.data.RelatedWord
import com.example.logilearnapp.data.WordOfTheDayResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel: ViewModel() {

    private val apiService = ApiClient.wordnikService


    fun getDefinitions(apiKey: String, palabra: String, callback: (List<Definition>?) -> Unit) {
        val call = apiService.getDefinitions(palabra, apiKey)

        call.enqueue(object : Callback<List<Definition>> {
            override fun onResponse(call: Call<List<Definition>>, response: Response<List<Definition>>) {
                if (response.isSuccessful) {
                    val definitions = response.body()
                    callback(definitions)
                } else {
                    callback(null) // Devuelve null en caso de error
                }
            }

            override fun onFailure(call: Call<List<Definition>>, t: Throwable) {
                callback(null) // Devuelve null en caso de fallo
            }
        })
    }

    fun getRelatedWords(apiKey: String, palabra: String, callback: (List<RelatedWord>?)-> Unit){
        val call = apiService.getRelatedWords(palabra, apiKey)
        call.enqueue(object : Callback<List<RelatedWord>> {
            override fun onResponse(call: Call<List<RelatedWord>>, response: Response<List<RelatedWord>>) {
                if (response.isSuccessful) {
                    val definitions = response.body()
                    callback(definitions)
                } else {
                    callback(null) // Devuelve null en caso de error
                }
            }
            override fun onFailure(call: Call<List<RelatedWord>>, t: Throwable) {
                callback(null) // Devuelve null en caso de fallo
            }
        })
    }
    fun getExamples(apiKey: String, palabra: String, callback: (Examples?)->Unit){
        val call = apiService.getExamples(palabra, apiKey)
        call.enqueue(object : Callback<Examples> {
            override fun onResponse(call: Call<Examples>, response: Response<Examples>) {
                if (response.isSuccessful) {
                    val examples = response.body()
                    callback(examples)
                } else {
                    callback(null) // Devuelve null en caso de error
                }
            }

            override fun onFailure(call: Call<Examples>, t: Throwable) {
                callback(null) // Devuelve null en caso de fallo
            }
        })

    }
    fun getPronunciations(apiKey: String, palabra: String, callback: (List<Pronunciation>?) -> Unit) {
        val call = apiService.getPronunciation(palabra, apiKey)
        call.enqueue(object : Callback<List<Pronunciation>> {
            override fun onResponse(call: Call<List<Pronunciation>>, response: Response<List<Pronunciation>>) {
                if (response.isSuccessful) {
                    val definitions = response.body()
                    callback(definitions)
                } else {
                    callback(null) // Devuelve null en caso de error
                }
            }

            override fun onFailure(call: Call<List<Pronunciation>>, t: Throwable) {
                callback(null) // Devuelve null en caso de fallo
            }
        })
    }


    fun getRandomWord(apiKey: String, callback: (WordOfTheDayResponse?) -> Unit) {
        val call = apiService.getWordOfTheDay( apiKey)

        call.enqueue(object : Callback<WordOfTheDayResponse> {
            override fun onResponse(call: Call<WordOfTheDayResponse>, response: Response<WordOfTheDayResponse>) {
                if (response.isSuccessful) {
                    val wordOfTheDay  = response.body()
                    callback(wordOfTheDay)
                } else {
                    callback(null) // Devuelve null en caso de error
                }
            }
            override fun onFailure(call: Call<WordOfTheDayResponse>, t: Throwable) {
                callback(null) // Devuelve null en caso de fallo
            }
        })
    }
}





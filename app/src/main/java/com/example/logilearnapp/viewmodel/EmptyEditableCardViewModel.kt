package com.example.logilearnapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.logilearnapp.api.ApiClient
import com.example.logilearnapp.api.ConfigUtils
import com.example.logilearnapp.api.DeeplApiClient
import com.example.logilearnapp.data.TranslateRequest
import com.example.logilearnapp.data.TranslateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmptyEditableCardViewModel :ViewModel() {
    private val apiService = DeeplApiClient.deepLService
    //añadir la api key
    private final val AUTHORIZATION = "DeepL-Auth-Key "
    private final val USERAGENT = "YourApp/1.2.3"
    private final val CONTENTTYPE = "application/json"

    fun postTranslation(requestBody: TranslateRequest,callback: TranslationCallback, apiKey:String){
        val call = apiService.translate(AUTHORIZATION + apiKey ,CONTENTTYPE, requestBody)
        call.enqueue(object : Callback<TranslateResponse> {
            override fun onResponse(call: Call<TranslateResponse>, response: Response<TranslateResponse>) {
                if (response.isSuccessful) {
                    val translateResponse = response.body()
                    callback(translateResponse?.translations?.get(0)?.translatedText)
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    Log.e("TranslationError", "Error al traducir: ${response.code()}  $errorBodyString")
                    callback(null)
                }
            }
            override fun onFailure(call: Call<TranslateResponse>, t: Throwable) {
                Log.e("TranslationError", "Error al traducir", t)
                callback(null)
            }
        })

    }
}

// Define un tipo de función de callback para manejar la respuesta de la traducción
typealias TranslationCallback = (String?) -> Unit
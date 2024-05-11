package com.example.logilearnapp.data

import com.google.gson.annotations.SerializedName

data class TranslateRequest(
    @SerializedName("text")
    val text: String,
    @SerializedName("target_lang")
    val targetLanguage: String
)

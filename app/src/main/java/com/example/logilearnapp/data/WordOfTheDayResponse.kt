package com.example.logilearnapp.data

import com.google.gson.annotations.SerializedName

data class WordOfTheDayResponse(
    @SerializedName("word") val word: String,
    @SerializedName("definition") val definition: String,
    @SerializedName("partOfSpeech") val partOfSpeech: String,
    @SerializedName("publishDate") val publishDate: String
)

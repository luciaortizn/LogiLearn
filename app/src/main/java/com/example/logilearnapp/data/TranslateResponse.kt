package com.example.logilearnapp.data

import com.google.gson.annotations.SerializedName

data class TranslateResponse(
    @SerializedName("translations")
    val translations: List<Translation>)
data class Translation(
    @SerializedName("detected_source_language")
    val detectedSourceLanguage: String?,
    @SerializedName("text")
    val translatedText: String
)
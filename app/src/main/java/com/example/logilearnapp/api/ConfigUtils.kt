package com.example.logilearnapp.api

import android.content.Context
import android.widget.Toast
import java.io.IOException
import java.util.Properties

object ConfigUtils {
    fun getDeeplApiKey(context: Context): String? {
        return try {
            val properties = Properties()
            val inputStream = context.assets.open("api.properties")
            properties.load(inputStream)
            properties.getProperty("deepl.api.key")
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    fun getWordnikApiKey(context: Context): String? {
        return try {
            val properties = Properties()
            val inputStream = context.assets.open("api.properties")
            properties.load(inputStream)
            properties.getProperty("wordnik.api.key")
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
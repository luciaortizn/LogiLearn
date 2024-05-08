package com.example.logilearnapp.database

import com.example.logilearnapp.ui.card.Card

interface FirebaseCallback {
    fun onCallback(cardList:ArrayList<Card>)
}
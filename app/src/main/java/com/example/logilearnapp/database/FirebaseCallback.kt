package com.example.logilearnapp.database

import com.example.logilearnapp.data.UserData
import com.example.logilearnapp.data.Label
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.ui.folder.Folder

interface FirebaseCallback {
    fun onCallback(cardList:ArrayList<Card>)
    fun onLabelNameCallback(cardList:ArrayList<Label>)
    fun onSingleUserCallback(user: UserData)
    fun onFolderCallback(folderList:ArrayList<Folder>)
}
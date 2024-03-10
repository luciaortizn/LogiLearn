package com.example.logilearnapp.ui.folder

import android.icu.text.CaseMap.Title
import android.media.Image

/*
class Folder (private var name:String, private var cardList: MutableList<Card>, private var label:Label) {
    //los getters y setters se crean por defecto + constructores primarios

}
* */

data class Folder(val dataImage: Int, var dataTitle: String)


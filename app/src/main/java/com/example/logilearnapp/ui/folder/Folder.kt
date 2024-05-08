package com.example.logilearnapp.ui.folder

import android.icu.text.CaseMap.Title
import android.media.Image

/*
class Folder (private var name:String, private var cardList: MutableList<Card>, private var label:Label) {
    //los getters y setters se crean por defecto + constructores primarios

}modificar parámetros: folders: title lista de cardIDs, label, favorite
* */

data class Folder(val id:String,val dataImage: Int, var dataTitle: String, var cardId:String){
    constructor() : this("",0, "", "")
}



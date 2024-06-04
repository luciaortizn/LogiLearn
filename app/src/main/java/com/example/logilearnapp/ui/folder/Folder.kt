package com.example.logilearnapp.ui.folder

import android.icu.text.CaseMap.Title
import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import com.example.logilearnapp.data.CardWithDifficulty

/*
class Folder (private var name:String, private var cardList: MutableList<Card>, private var label:Label) {
    //los getters y setters se crean por defecto + constructores primarios

}modificar parámetros: folders: title lista de cardIDs, label, favorite
* */

data class Folder
    (val id:String,
     var isFavorite:String,
     var dataTitle: String,
     var cardId: ArrayList<CardWithDifficulty>?)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(CardWithDifficulty.CREATOR)!!
    )

    constructor() : this("","false", "", ArrayList())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(isFavorite)
        parcel.writeString(dataTitle)
    }

    companion object CREATOR : Parcelable.Creator<Folder> {
        override fun createFromParcel(parcel: Parcel): Folder {
            return Folder(parcel)
        }

        override fun newArray(size: Int): Array<Folder?> {
            return arrayOfNulls(size)
        }
    }
}



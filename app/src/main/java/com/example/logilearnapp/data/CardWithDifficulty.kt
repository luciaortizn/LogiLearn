package com.example.logilearnapp.data

import android.os.Parcel
import android.os.Parcelable

data class CardWithDifficulty( val cardId: String = "",
                               val difficulty: Difficulty = Difficulty.EASY): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Difficulty::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cardId)
        parcel.writeParcelable(difficulty, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<CardWithDifficulty> {
        override fun createFromParcel(parcel: Parcel) = CardWithDifficulty(parcel)
        override fun newArray(size: Int) = arrayOfNulls<CardWithDifficulty>(size)
    }
                              }

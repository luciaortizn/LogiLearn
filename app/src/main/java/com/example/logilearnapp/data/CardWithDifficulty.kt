package com.example.logilearnapp.data

import android.os.Parcel
import android.os.Parcelable

data class CardWithDifficulty(val cardId: String = "",
                              var difficulty: Difficulty = Difficulty.EASY,
                                var repeated :Int = 0): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Difficulty::class.java.classLoader)!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cardId);
        parcel.writeParcelable(difficulty, flags);
        parcel.writeInt(repeated)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<CardWithDifficulty> {
        override fun createFromParcel(parcel: Parcel) = CardWithDifficulty(parcel)
        override fun newArray(size: Int) = arrayOfNulls<CardWithDifficulty>(size)
    }
                                }

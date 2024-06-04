package com.example.logilearnapp.data

import android.os.Parcel
import android.os.Parcelable

enum class Difficulty : Parcelable {
    EASY,
    REGULAR,
    HARD;

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Difficulty> {
            override fun createFromParcel(parcel: Parcel) = values()[parcel.readInt()]
            override fun newArray(size: Int) = arrayOfNulls<Difficulty>(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordinal)
    }

    override fun describeContents() = 0

}

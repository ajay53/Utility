package com.goazi.utility.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_unlock")
data class Unlock(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val path: String?,
    val date: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(path)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Unlock> {
        override fun createFromParcel(parcel: Parcel): Unlock {
            return Unlock(parcel)
        }

        override fun newArray(size: Int): Array<Unlock?> {
            return arrayOfNulls(size)
        }
    }

}
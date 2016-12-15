package ru.barsopen.testdb.domain.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by d.sokolov on 13.12.2016.
 */
class Item constructor(val id:Int, val title:String, val desc:String, val date:Long) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Item> = object : Parcelable.Creator<Item> {
            override fun createFromParcel(source: Parcel): Item = Item(source)
            override fun newArray(size: Int): Array<Item?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readInt(), source.readString(), source.readString(), source.readLong())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(title)
        dest?.writeString(desc)
        dest?.writeLong(date)
    }
}
package ru.barsopen.testdb.rx_events

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by d.sokolov on 13.12.2016.
 */
data class PositionChanged constructor(val from:Int, val to:Int) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<PositionChanged> = object : Parcelable.Creator<PositionChanged> {
            override fun createFromParcel(source: Parcel): PositionChanged = PositionChanged(source)
            override fun newArray(size: Int): Array<PositionChanged?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readInt(), source.readInt())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(from)
        dest?.writeInt(to)
    }
}
package com.qkopy.gallery.model

import android.os.Parcel
import android.os.Parcelable

class Image : Parcelable {
    var id: Long
    var name: String
    var path: String
    var selected = false

    //    public Image(long id, String name, String path) {
    //        this.id = id;
    //        this.name = name;
    //        this.path = path;
    //    }
    constructor(
        id: Long,
        name: String,
        path: String,
        isSelected: Boolean
    ) {
        this.id = id
        this.name = name
        this.path = path
        selected = isSelected
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readLong()
        name = `in`.readString()
        path = `in`.readString()
        val myBooleanArr = BooleanArray(1)
        `in`.readBooleanArray(myBooleanArr)
        selected = myBooleanArr[0]
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(path)
        dest.writeBooleanArray(booleanArrayOf(selected))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Image> =
            object : Parcelable.Creator<Image> {
                override fun createFromParcel(source: Parcel): Image? {
                    return Image(source)
                }

                override fun newArray(size: Int): Array<Image?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
package com.qkopy.gallery.model

import android.os.Parcel
import android.os.Parcelable
import com.qkopy.gallery.BuildConfig


class SavePath : Parcelable {
    val path: String
    val isFullPath: Boolean

    constructor(path: String, isFullPath: Boolean) {
        this.path = path
        this.isFullPath = isFullPath
    }

    protected constructor(`in`: Parcel) {
        path = `in`.readString()
        isFullPath = `in`.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(path)
        dest.writeByte(if (isFullPath) 1.toByte() else 0.toByte())
    }

    companion object {
        val DEFAULT = SavePath(BuildConfig.IMAGE_PATH, false)
        @JvmField
        val CREATOR: Parcelable.Creator<SavePath> = object : Parcelable.Creator<SavePath> {
            override fun createFromParcel(source: Parcel): SavePath? {
                return SavePath(source)
            }

            override fun newArray(size: Int): Array<SavePath?> {
                return arrayOfNulls(size)
            }
        }
    }
}

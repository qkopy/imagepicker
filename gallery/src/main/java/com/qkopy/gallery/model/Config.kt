/*
 * Created by @ajithvgiri on 4/12/18 8:07 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */
package com.qkopy.gallery.model

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import kotlin.collections.ArrayList

class Config : Parcelable {
    private var toolbarColor: String? = null
    private var statusBarColor: String? = null
    private var toolbarTextColor: String? = null
    private var toolbarIconColor: String? = null
    private var progressBarColor: String? = null
    private var backgroundColor: String? = null
    var isCameraOnly = false
    var isMultipleMode = false
    var isFolderMode = false
    var isShowCamera = false
    var maxSize = 0
    var doneTitle: String? = null
    var folderTitle: String? = null
    var imageTitle: String? = null
    var limitMessage: String? = null
    var savePath: SavePath? = null
    var isAlwaysShowDoneButton = false
    var isKeepScreenOn = false
    var requestCode = 0
    var selectedImages: ArrayList<Image>? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        toolbarColor = `in`.readString()
        statusBarColor = `in`.readString()
        toolbarTextColor = `in`.readString()
        toolbarIconColor = `in`.readString()
        progressBarColor = `in`.readString()
        backgroundColor = `in`.readString()
        isCameraOnly = `in`.readByte().toInt() != 0
        isMultipleMode = `in`.readByte().toInt() != 0
        isFolderMode = `in`.readByte().toInt() != 0
        isShowCamera = `in`.readByte().toInt() != 0
        maxSize = `in`.readInt()
        doneTitle = `in`.readString()
        folderTitle = `in`.readString()
        imageTitle = `in`.readString()
        limitMessage = `in`.readString()
        savePath = `in`.readParcelable(SavePath::class.java.classLoader)
        isAlwaysShowDoneButton = `in`.readByte().toInt() != 0
        isKeepScreenOn = `in`.readByte().toInt() != 0
        requestCode = `in`.readInt()
        selectedImages = `in`.createTypedArrayList(Image.CREATOR)
    }

    fun getToolbarColor(): Int {
        return if (TextUtils.isEmpty(toolbarColor)) {
            Color.parseColor("#212121")
        } else Color.parseColor(toolbarColor)
    }

    fun setToolbarColor(toolbarColor: String?) {
        this.toolbarColor = toolbarColor
    }

    fun getStatusBarColor(): Int {
        return if (TextUtils.isEmpty(statusBarColor)) {
            Color.parseColor("#000000")
        } else Color.parseColor(statusBarColor)
    }

    fun setStatusBarColor(statusBarColor: String?) {
        this.statusBarColor = statusBarColor
    }

    fun getToolbarTextColor(): Int {
        return if (TextUtils.isEmpty(toolbarTextColor)) {
            Color.parseColor("#FFFFFF")
        } else Color.parseColor(toolbarTextColor)
    }

    fun setToolbarTextColor(toolbarTextColor: String?) {
        this.toolbarTextColor = toolbarTextColor
    }

    fun getToolbarIconColor(): Int {
        return if (TextUtils.isEmpty(toolbarIconColor)) {
            Color.parseColor("#FFFFFF")
        } else Color.parseColor(toolbarIconColor)
    }

    fun setToolbarIconColor(toolbarIconColor: String?) {
        this.toolbarIconColor = toolbarIconColor
    }

    fun getProgressBarColor(): Int {
        return if (TextUtils.isEmpty(progressBarColor)) {
            Color.parseColor("#4CAF50")
        } else Color.parseColor(progressBarColor)
    }

    fun setProgressBarColor(progressBarColor: String?) {
        this.progressBarColor = progressBarColor
    }

    fun getBackgroundColor(): Int {
        return if (TextUtils.isEmpty(backgroundColor)) {
            Color.parseColor("#212121")
        } else Color.parseColor(backgroundColor)
    }

    fun setBackgroundColor(backgroundColor: String?) {
        this.backgroundColor = backgroundColor
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(toolbarColor)
        dest.writeString(statusBarColor)
        dest.writeString(toolbarTextColor)
        dest.writeString(toolbarIconColor)
        dest.writeString(progressBarColor)
        dest.writeString(backgroundColor)
        dest.writeByte(if (isCameraOnly) 1.toByte() else 0.toByte())
        dest.writeByte(if (isMultipleMode) 1.toByte() else 0.toByte())
        dest.writeByte(if (isFolderMode) 1.toByte() else 0.toByte())
        dest.writeByte(if (isShowCamera) 1.toByte() else 0.toByte())
        dest.writeInt(maxSize)
        dest.writeString(doneTitle)
        dest.writeString(folderTitle)
        dest.writeString(imageTitle)
        dest.writeString(limitMessage)
        dest.writeParcelable(savePath, flags)
        dest.writeByte(if (isAlwaysShowDoneButton) 1.toByte() else 0.toByte())
        dest.writeByte(if (isKeepScreenOn) 1.toByte() else 0.toByte())
        dest.writeInt(requestCode)
        dest.writeTypedList(selectedImages)
    }

    companion object {
        const val EXTRA_CONFIG = "GalleryConfig"
        const val EXTRA_IMAGES = "GalleryImages"
        const val RC_PICK_IMAGES = 100
        const val RC_CAPTURE_IMAGE = 101
        const val RC_WRITE_EXTERNAL_STORAGE_PERMISSION = 102
        const val RC_CAMERA_PERMISSION = 103
        const val MAX_SIZE = Int.MAX_VALUE

        @JvmField
        val CREATOR: Parcelable.Creator<Config?> =
            object : Parcelable.Creator<Config?> {
                override fun createFromParcel(source: Parcel): Config? {
                    return Config(source)
                }

                override fun newArray(size: Int): Array<Config?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
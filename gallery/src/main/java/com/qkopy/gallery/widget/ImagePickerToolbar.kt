/*
 * Created by @ajithvgiri on 4/12/18 8:19 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.qkopy.gallery.R
import com.qkopy.gallery.model.Config
import kotlinx.android.synthetic.main.imagepicker_toolbar.view.*


class ImagePickerToolbar : RelativeLayout {

    private var titleText: TextView? = null
    private var doneText: TextView? = null
    private var selectedText: TextView? = null
    private var backImage: AppCompatImageView? = null
    private var cameraImage: AppCompatImageView? = null
    private lateinit var config: Config

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.imagepicker_toolbar, this)
        if (isInEditMode) {
            return
        }

        titleText = text_toolbar_title
        selectedText = text_toolbar_count
        doneText = text_toolbar_done
        backImage = image_toolbar_back
        cameraImage = image_toolbar_camera
    }

    fun config(config: Config) {
        this.config = config

        setBackgroundColor(config.getToolbarColor())

        titleText!!.text = if (config.isFolderMode) config.folderTitle else config.imageTitle
        titleText!!.setTextColor(config.getToolbarTextColor())

        doneText!!.text = config.doneTitle
        doneText!!.setTextColor(config.getToolbarTextColor())

       backImage!!.setColorFilter(config.getToolbarIconColor())

        cameraImage!!.setColorFilter(config.getToolbarIconColor())
        cameraImage!!.visibility = if (config.isShowCamera) View.VISIBLE else View.GONE

        doneText!!.visibility = View.GONE

        selectedText!!.text = "0/${config.maxSize}"
    }

    fun setTitle(title: String) {
        titleText!!.text = title
    }


    fun showDoneButton(isShow: Boolean) {
        doneText!!.visibility = if (isShow) View.VISIBLE else View.GONE
        selectedText!!.visibility = if (isShow) View.VISIBLE else View.GONE
        cameraImage!!.visibility = if (!isShow) View.VISIBLE else View.GONE
    }

    fun updateSelectedCount(count: Int) {
        selectedText!!.text = "$count/${config.maxSize}"
    }

    fun setOnBackClickListener(clickListener: View.OnClickListener) {
        backImage!!.setOnClickListener(clickListener)
    }

    fun setOnCameraClickListener(clickListener: View.OnClickListener) {
        cameraImage!!.setOnClickListener(clickListener)
    }

    fun setOnDoneClickListener(clickListener: View.OnClickListener) {
        doneText!!.setOnClickListener(clickListener)
    }

}

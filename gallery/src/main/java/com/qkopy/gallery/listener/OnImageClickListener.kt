/*
 * Created by @ajithvgiri on 4/12/18 8:04 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.listener

import android.view.View

interface OnImageClickListener {
    fun onImageClick(view: View, position: Int, isSelected: Boolean): Boolean
}

/*
 * Created by @ajithvgiri on 4/12/18 8:43 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.listener

import com.qkopy.gallery.model.Image


interface OnImageSelectionListener {
    fun onSelectionUpdate(images: List<Image>)
}

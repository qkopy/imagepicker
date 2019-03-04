/*
 * Created by @ajithvgiri on 4/12/18 8:07 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.model

import com.qkopy.gallery.model.Image
import java.util.*

class Folder(var folderName: String?) {
    var images: ArrayList<Image>? = null

    init {
        images = ArrayList()
    }
}

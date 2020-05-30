/*
 * Created by @ajithvgiri on 4/12/18 8:42 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.listener

import com.qkopy.gallery.model.Folder
import com.qkopy.gallery.model.Image

interface OnImageLoaderListener {

    fun onFolderAdded(images: List<Image>, folders: List<Folder>)

    fun onFolderUpdated(folders: Folder)

    fun onImageAdded(image: Image)

    fun onFailed(throwable: Throwable)

    fun onEmpty()
}

package com.qkopy.gallery.ui.camera

import com.qkopy.gallery.model.Image


interface OnImageReadyListener {
    fun onImageReady(images: List<Image>?)
}

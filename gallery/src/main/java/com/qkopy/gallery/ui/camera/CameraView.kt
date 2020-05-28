package com.qkopy.gallery.ui.camera

import com.qkopy.gallery.model.Image
import com.qkopy.gallery.ui.common.MvpView

interface CameraView : MvpView {
    fun finishPickImages(images: List<Image>?)
}

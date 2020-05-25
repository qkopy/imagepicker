package com.qkopy.gallery.ui.camera

import android.content.Context
import android.content.Intent
import com.qkopy.gallery.model.Config

interface CameraModule {
    fun getCameraIntent(
        context: Context?,
        config: Config?
    ): Intent?

    fun getImage(
        context: Context?,
        intent: Intent?,
        imageReadyListener: OnImageReadyListener?
    )
}

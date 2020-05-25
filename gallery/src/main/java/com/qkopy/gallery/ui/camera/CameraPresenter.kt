package com.qkopy.gallery.ui.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.qkopy.gallery.R
import com.qkopy.gallery.model.Config
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.ui.common.BasePresenter

class CameraPresenter : BasePresenter<CameraView?>() {
    private val cameraModule: CameraModule = DefaultCameraModule()
    fun captureImage(
        activity: Activity,
        config: Config?,
        requestCode: Int
    ) {
        val context = activity.applicationContext
        val intent = cameraModule.getCameraIntent(activity, config)
        if (intent == null) {
            Toast.makeText(
                context,
                context.getString(R.string.error_create_image_file),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        activity.startActivityForResult(intent, requestCode)
    }

    fun finishCaptureImage(
        context: Context?,
        data: Intent?,
        config: Config?
    ) {
        cameraModule.getImage(context, data, object : OnImageReadyListener {
            override fun onImageReady(images: List<Image>?) {
                view!!.finishPickImages(images)
            }
        })
    }
}

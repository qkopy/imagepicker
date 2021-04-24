package com.qkopy.gallery.ui.camera

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.qkopy.gallery.helper.ImageHelper
import com.qkopy.gallery.model.Config
import java.io.File
import java.io.Serializable
import java.util.*

class DefaultCameraModule : CameraModule, Serializable {
    protected var imagePath: String? = null


    override fun getCameraIntent(context: Context?, config: Config?): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile: File = ImageHelper.createImageFile(context!!, config!!.savePath!!)!!
        if (imageFile != null) {
            val appContext = context!!.applicationContext
            val providerName = String.format(
                Locale.ENGLISH,
                "%s%s",
                appContext.packageName,
                ".fileprovider"
            )
            val uri =
                FileProvider.getUriForFile(appContext, providerName, imageFile)
            imagePath = imageFile.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            ImageHelper.grantAppPermission(context, intent, uri)
            return intent
        }
        return null
    }

    override fun getImage(
        context: Context?,
        intent: Intent?,
        imageReadyListener: OnImageReadyListener?
    ) {
        checkNotNull(imageReadyListener) { "OnImageReadyListener must not be null" }
        if (imagePath == null) {
            imageReadyListener.onImageReady(null)
            return
        }
        val imageUri = Uri.parse(File(imagePath).toString())
        if (imageUri != null) {
            MediaScannerConnection.scanFile(
                context!!.applicationContext,
                arrayOf(imageUri.path),
                null
            ) { path, uri ->
                var path = path
                if (path != null) {
                    path = imagePath
                }
                imageReadyListener.onImageReady(ImageHelper.singleListFromPath(path))
                ImageHelper.revokeAppPermission(context, imageUri)
            }
        }
    }
}

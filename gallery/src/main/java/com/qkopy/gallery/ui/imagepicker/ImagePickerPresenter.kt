package com.qkopy.gallery.ui.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.qkopy.gallery.R
import com.qkopy.gallery.listener.OnImageLoaderListener
import com.qkopy.gallery.model.Config
import com.qkopy.gallery.model.Folder
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.ui.camera.CameraModule
import com.qkopy.gallery.ui.camera.DefaultCameraModule
import com.qkopy.gallery.ui.camera.OnImageReadyListener
import com.qkopy.gallery.ui.common.BasePresenter
import java.io.File

class ImagePickerPresenter(private val imageLoader: ImageFileLoader) :
    BasePresenter<ImagePickerView?>() {
    private val cameraModule: CameraModule = DefaultCameraModule()
    private val handler = Handler(Looper.getMainLooper())
    fun abortLoading() {
        imageLoader.abortLoadImages()
    }

    fun loadImages(isFolderMode: Boolean) {
        if (!isViewAttached) return
        view!!.showLoading(true)
        imageLoader.loadDeviceImages(isFolderMode, object : OnImageLoaderListener {
            override fun onImageLoaded(
                images: List<Image>,
                folders: List<Folder>
            ) {
                handler.post {
                    if (isViewAttached) {
                        view!!.showFetchCompleted(
                            images,
                            folders
                        )
                        val isEmpty =
                            folders.isEmpty() and  images.isEmpty()
                        if (isEmpty) {
                            view!!.showEmpty()
                        } else {
                            view!!.showLoading(false)
                        }
                    }
                }
            }

            override fun onFolderAdded(images: List<Image>, folders: List<Folder>) {
                handler.post {
                    if (isViewAttached){
                        view!!.showFetching(images,folders)
                        val isEmpty =
                            folders.isEmpty()
                        if (isEmpty) {
                            view!!.showEmpty()
                        } else {
                            view!!.showLoading(false)
                        }
                    }
                }
            }

            override fun onFolderUpdated(images: List<Image>, folder: Folder) {
                handler.post {
                    if (isViewAttached){
                        view!!.showUpdate(images.last(),folder)
                    }
                }
            }

            override fun onFailed(throwable: Throwable) {
                handler.post {
                    if (isViewAttached) {
                        view!!.showError(throwable)
                    }
                }
            }
        })
    }

    fun captureImage(
        activity: Activity,
        config: Config?,
        requestCode: Int
    ) {
        val context = activity.applicationContext
        val intent = cameraModule.getCameraIntent(activity,config)
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
        config: Config
    ) {
        cameraModule.getImage(context, data, object : OnImageReadyListener {

            override fun onImageReady(images: List<Image>?) {
                if (!config.isMultipleMode) {
                    view!!.finishPickImages(images)
                } else {
                    //disable this
                    view!!.finishPickImages(images)
                    //getView().showCapturedImage(images);
                }
            }

        })
    }

    fun onDoneSelectImages(selectedImages: ArrayList<Image>) {
        if (selectedImages != null && !selectedImages.isEmpty()) {
            var i = 0
            while (i < selectedImages.size) {
                val image = selectedImages[i]
                val file = File(image.path)
                if (!file.exists()) {
                    selectedImages.removeAt(i)
                    i--
                }
                i++
            }
        }
        view!!.finishPickImages(selectedImages)
    }

}
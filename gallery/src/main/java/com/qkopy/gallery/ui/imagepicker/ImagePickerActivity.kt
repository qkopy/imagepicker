/*
 * Created by @ajithvgiri on 4/12/18 8:14 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.ui.imagepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.qkopy.gallery.R
import com.qkopy.gallery.helper.CameraHelper
import com.qkopy.gallery.helper.LogHelper
import com.qkopy.gallery.helper.PermissionHelper
import com.qkopy.gallery.listener.OnBackAction
import com.qkopy.gallery.listener.OnFolderClickListener
import com.qkopy.gallery.listener.OnImageClickListener
import com.qkopy.gallery.listener.OnImageSelectionListener
import com.qkopy.gallery.model.Config
import com.qkopy.gallery.model.Folder
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.widget.ImagePickerToolbar
import com.qkopy.gallery.widget.ProgressWheel
import com.qkopy.gallery.widget.SnackBarView
import kotlinx.android.synthetic.main.imagepicker_activity_picker.*
import java.util.*


class ImagePickerActivity : AppCompatActivity(), ImagePickerView {

    private lateinit var toolbar: ImagePickerToolbar
    private lateinit var recyclerViewManager: RecyclerViewManager
    private lateinit var progressWheel: ProgressWheel
    private lateinit var emptyLayout: View
    private lateinit var snackBar: SnackBarView

    private lateinit var config: Config
    private var handler: Handler? = null
    private var observer: ContentObserver? = null
    private lateinit var presenter: ImagePickerPresenter
    private val logger = LogHelper.instance


    private val imageClickListener = object : OnImageClickListener {
        override fun onImageClick(view: View, position: Int, isSelected: Boolean): Boolean {
            return recyclerViewManager.selectImage()
        }
    }

    private val folderClickListener = object : OnFolderClickListener {
        override fun onFolderClick(folder: Folder) {
            folder.images?.let { folder.folderName?.let { it1 -> setImageAdapter(it, it1) } }
        }
    }


    private val backClickListener = View.OnClickListener { onBackPressed() }

    private val cameraClickListener = View.OnClickListener { captureImageWithPermission() }

    private val doneClickListener = View.OnClickListener { onDone() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        if (intent == null) {
            finish()
            return
        }

        config = intent.getParcelableExtra(Config.EXTRA_CONFIG)
        if (config.isKeepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        setContentView(R.layout.imagepicker_activity_picker)

        setupView()
        setupComponents()
        setupToolbar()

    }

    private fun setupView() {
        toolbar = findViewById(R.id.toolbar)
        progressWheel = findViewById(R.id.progressWheel)
        emptyLayout = findViewById(R.id.layout_empty)
        snackBar = findViewById(R.id.snackbar)

        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = config.getStatusBarColor()
        }

        progressWheel.setBarColor(config.getProgressBarColor())
        findViewById<View>(R.id.container).setBackgroundColor(config.getBackgroundColor())


    }

    private fun setupComponents() {
        recyclerViewManager =
            RecyclerViewManager(recyclerView!!, config, resources.configuration.orientation)
        recyclerViewManager.setupAdapters(imageClickListener, folderClickListener)
        recyclerViewManager.setOnImageSelectionListener(object : OnImageSelectionListener {
            override fun onSelectionUpdate(images: List<Image>) {
                invalidateToolbar()
                if (!config.isMultipleMode && !images.isEmpty()) {
                    onDone()
                }
            }
        })

        presenter = ImagePickerPresenter(ImageFileLoader(this))
        presenter.attachView(this)
    }

    private fun setupToolbar() {
        toolbar.let { imagePickerToolbar ->
            config.let { imagePickerToolbar.config(it) }
            imagePickerToolbar.setOnBackClickListener(backClickListener)
            imagePickerToolbar.setOnCameraClickListener(cameraClickListener)
            imagePickerToolbar.setOnDoneClickListener(doneClickListener)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataWithPermission()
    }


    private fun setImageAdapter(images: List<Image>, title: String) {
        recyclerViewManager.setImageAdapter(images, title)
        invalidateToolbar()
    }


    private fun setFolderAdapter(folders: List<Folder>) {
        recyclerViewManager.setFolderAdapter(folders)
        invalidateToolbar()
    }

    private fun addToFolderAdapter(folder: Folder) {
        recyclerViewManager.addToFolderAdapter(folder)
        invalidateToolbar()
    }

    private fun updateFolderAdapter(folder: Folder) {
        recyclerViewManager.updateFolderAdapter(folder)
    }

    private fun updateImagesAdapter(images: Image) {
        recyclerViewManager.addToImageAdapter(images)
    }

    private fun invalidateToolbar() {
        toolbar.setTitle(recyclerViewManager.getTitle()!!)
        toolbar.showDoneButton(recyclerViewManager.isShowDoneButton)
        toolbar.updateSelectedCount(recyclerViewManager.selectedMediaCount())

    }

    private fun onDone() {
        presenter.onDoneSelectImages(recyclerViewManager.selectedImages)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recyclerViewManager.changeOrientation(newConfig.orientation)
    }


    private fun getDataWithPermission() {

        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        PermissionHelper.checkPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            object : PermissionHelper.PermissionAskListener {
                override fun onNeedPermission() {
                    PermissionHelper.requestAllPermissions(
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION
                    )
                }

                override fun onPermissionPreviouslyDenied() {
                    PermissionHelper.requestAllPermissions(
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION
                    )
                }

                override fun onPermissionDisabled() {
                    snackBar.show(R.string.msg_no_write_external_storage_permission,
                        object : View.OnClickListener {
                            override fun onClick(v: View?) {
                                PermissionHelper.openAppSettings(
                                    this@ImagePickerActivity
                                )
                            }
                        })
                }

                override fun onPermissionGranted() {
                    getData()
                }
            })
    }

    private fun getData() {
        presenter.abortLoading()
        presenter.loadImages(config.isFolderMode)
    }


    private fun captureImageWithPermission() {

        val permissions = arrayOf(Manifest.permission.CAMERA)

        PermissionHelper.checkPermission(
            this,
            Manifest.permission.CAMERA,
            object : PermissionHelper.PermissionAskListener {
                override fun onNeedPermission() {
                    PermissionHelper.requestAllPermissions(
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_CAMERA_PERMISSION
                    )
                }

                override fun onPermissionPreviouslyDenied() {
                    PermissionHelper.requestAllPermissions(
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_CAMERA_PERMISSION
                    )
                }

                override fun onPermissionDisabled() {
                    snackBar.show(R.string.msg_no_camera_permission,
                        object : View.OnClickListener {
                            override fun onClick(v: View?) {
                                PermissionHelper.openAppSettings(
                                    this@ImagePickerActivity
                                )
                            }
                        })
                }

                override fun onPermissionGranted() {
                    captureImage()
                }
            })
    }


    private fun captureImage() {
        if (!CameraHelper.checkCameraAvailability(this)) {
            return
        }
        presenter.captureImage(this, config, Config.RC_CAPTURE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Config.RC_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            presenter.finishCaptureImage(this, data, config)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {

        when (requestCode) {
            Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                run {
                    if (PermissionHelper.hasGranted(grantResults)) {
                        if (logger != null) {
                            logger.d("Write External permission granted")
                        }
                        getData()
                        return
                    }
                    if (logger != null) {
                        logger.e(
                            "Permission not granted: results len = " + grantResults.size +
                                    " Result code = " + if (grantResults.isNotEmpty()) grantResults[0] else "(empty)"
                        )
                    }
                    finish()
                }
                run {
                    if (PermissionHelper.hasGranted(grantResults)) {
                        if (logger != null) {
                            logger.d("Camera permission granted")
                        }
                        captureImage()
                        return
                    }
                    if (logger != null) {
                        logger.e(
                            "Permission not granted: results len = " + grantResults.size +
                                    " Result code = " + if (grantResults.isNotEmpty()) grantResults[0] else "(empty)"
                        )
                    }

                }
            }
            Config.RC_CAMERA_PERMISSION -> {
                if (PermissionHelper.hasGranted(grantResults)) {
                    if (logger != null) {
                        logger.d("Camera permission granted")
                    }
                    captureImage()
                    return
                }
                if (logger != null) {
                    logger.e("Permission not granted: results len = " + grantResults.size + " Result code = " + if (grantResults.isNotEmpty()) grantResults[0] else "(empty)")
                }
            }
            else -> {
                if (logger != null) {
                    logger.d("Got unexpected permission result: $requestCode")
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (handler == null) {
            handler = Handler()
        }
        observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                getData()
            }
        }
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            false,
            observer!!
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        if (presenter != null) {
            presenter.abortLoading()
            presenter.detachView()
        }

        if (observer != null) {
            contentResolver.unregisterContentObserver(observer)
            observer = null
        }

        if (handler != null) {
            handler!!.removeCallbacksAndMessages(null)
            handler = null
        }
    }

    override fun onBackPressed() {
        recyclerViewManager.handleBack(object : OnBackAction {
            override fun onBackToFolder() {
                invalidateToolbar()
            }

            override fun onFinishImagePicker() {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        })
    }

    /**
     * MVP view methods
     */

    override fun showLoading(isLoading: Boolean) {
        progressWheel.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        emptyLayout.visibility = View.GONE
    }

    //Here set data to adapter and add data to adapter when an image is found
    override fun showFetching(images: List<Image>?, folders: List<Folder>?) {
        if (config.isFolderMode) {
            if (folders?.size ?: 0 == 1)
                setFolderAdapter(folders!!)
            else if (folders?.size ?: 0 > 1)
                addToFolderAdapter(folders!!.last())
        }
    }

    //Here Update the images in a folder when an image is found
    override fun showUpdateFolder(folder: Folder) {
        if (config.isFolderMode)
            updateFolderAdapter(folder)
    }

    //Here update images on images found in Images list
    override fun showUpdateImage(images: List<Image>?) {
        if (!config.isFolderMode) {
            if (images?.size ?: 0 == 1)
                setImageAdapter(images!!, config.imageTitle!!)
            else if (images?.size ?: 0 > 1)
                updateImagesAdapter(images!!.last())
        }
    }

    override fun showError(throwable: Throwable?) {
        var message = getString(R.string.error_unknown)
        if (throwable != null && throwable is NullPointerException) {
            message = getString(R.string.error_images_not_exist)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showEmpty() {
        progressWheel.visibility = View.GONE
        recyclerView!!.visibility = View.GONE
        emptyLayout.visibility = View.VISIBLE
    }

    override fun showCapturedImage(images: List<Image>?) {
        val shouldSelect = recyclerViewManager.selectImage()
        if (shouldSelect) {
            recyclerViewManager.addSelectedImages(images)
        }
        getDataWithPermission()
    }

    override fun finishPickImages(images: List<Image>?) {
        val data = Intent()
        data.putParcelableArrayListExtra(Config.EXTRA_IMAGES, images as ArrayList<out Parcelable>)
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}


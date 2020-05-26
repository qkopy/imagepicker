package com.qkopy.gallery.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.qkopy.gallery.R
import com.qkopy.gallery.helper.CameraHelper
import com.qkopy.gallery.helper.LogHelper
import com.qkopy.gallery.helper.PermissionHelper
import com.qkopy.gallery.helper.PermissionHelper.openAppSettings
import com.qkopy.gallery.helper.PreferenceHelper
import com.qkopy.gallery.model.Config
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.widget.SnackBarView
import java.util.*


class CameraActivty : AppCompatActivity(), CameraView {
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private var snackBar: SnackBarView? = null
    lateinit var config: Config
    private var presenter: CameraPresenter? = null
    private val logger = LogHelper.instance
    private var isOpeningCamera = false
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
        setContentView(R.layout.imagepicker_activity_camera)
        snackBar = findViewById(R.id.snackbar)
        presenter = CameraPresenter()
        presenter!!.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        if (PermissionHelper.hasSelfPermissions(this, permissions) && isOpeningCamera) {
            isOpeningCamera = false
        } else if (!snackBar!!.isShowing) {
            captureImageWithPermission()
        }
    }

    private fun captureImageWithPermission() {
        if (PermissionHelper.hasSelfPermissions(this, permissions)) {
            captureImage()
        } else {
            if (logger != null) {
                logger.w("Camera permission is not granted. Requesting permission")
            }
            requestCameraPermission()
        }
    }

    private fun captureImage() {
        if (!CameraHelper.checkCameraAvailability(this)) {
            finish()
            return
        }
        presenter!!.captureImage(this, config, Config.RC_CAPTURE_IMAGE)
        isOpeningCamera = true
    }

    private fun requestCameraPermission() {
        if (logger != null) {
            logger.w("Write External permission is not granted. Requesting permission")
        }
        var hasPermissionDisbled = false
        val wesGranted = PermissionHelper.hasSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val cameraGranted =
            PermissionHelper.hasSelfPermission(this, Manifest.permission.CAMERA)
        if (!wesGranted && !PermissionHelper.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            if (!PreferenceHelper.isFirstTimeAskingPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                hasPermissionDisbled = true
            }
        }
        if (!cameraGranted && !PermissionHelper.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
            if (!PreferenceHelper.isFirstTimeAskingPermission(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                hasPermissionDisbled = true
            }
        }
        val permissions: MutableList<String> =
            ArrayList()
        if (!hasPermissionDisbled) {
            if (!wesGranted) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                PreferenceHelper.firstTimeAskingPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    false
                )
            }
            if (!cameraGranted) {
                permissions.add(Manifest.permission.CAMERA)
                PreferenceHelper.firstTimeAskingPermission(
                    this,
                    Manifest.permission.CAMERA,
                    false
                )
            }
            PermissionHelper.requestAllPermissions(
                this,
                permissions.toTypedArray(),
                Config.RC_CAMERA_PERMISSION
            )
        } else {
            snackBar!!.show(
                R.string.msg_no_write_external_storage_camera_permission,
                object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        PermissionHelper.openAppSettings(this@CameraActivty)
                    }
                })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Config.RC_CAMERA_PERMISSION -> {
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
                                " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)"
                    )
                }
                var shouldShowSnackBar = false
                for (grantResult in grantResults) {
                    if (PermissionHelper.hasGranted(grantResult)) {
                        shouldShowSnackBar = true
                        break
                    }
                }
                if (shouldShowSnackBar) {
                    snackBar!!.show(
                        R.string.msg_no_write_external_storage_camera_permission,
                        object : View.OnClickListener {
                            override fun onClick(view: View?) {
                                openAppSettings(this@CameraActivty)
                            }
                        })
                } else {
                    finish()
                }
            }
            else -> {
                if (logger != null) {
                    logger.d("Got unexpected permission result: $requestCode")
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                finish()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == Config.RC_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            presenter!!.finishCaptureImage(this, data, config)
        } else {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null) {
            presenter!!.detachView()
        }
    }



    override fun finishPickImages(images: List<Image>?) {
        val data = Intent()
        data.putParcelableArrayListExtra(
            Config.EXTRA_IMAGES,
            images as ArrayList<out Parcelable?>
        )
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}

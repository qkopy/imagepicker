package com.qkopy.gallery.ui.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.qkopy.gallery.R
import com.qkopy.gallery.model.Config
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.model.SavePath
import com.qkopy.gallery.ui.camera.CameraActivty

class ImagePicker(builder: Builder) {
    protected var config: Config

    internal class ActivityBuilder(private val activity: Activity) : Builder(activity) {
        override fun start() {
            val intent = intent!!
            val requestCode: Int =
                if (config.requestCode !== 0) config.requestCode else Config.RC_PICK_IMAGES
            if (!config.isCameraOnly) {
                activity.startActivityForResult(intent, requestCode)
            } else {
                activity.overridePendingTransition(0, 0)
                activity.startActivityForResult(intent, requestCode)
            }
        }

        override val intent: Intent?
            get() {
                val intent: Intent
                if (!config.isCameraOnly) {
                    intent = Intent(activity, ImagePickerActivity::class.java)
                    intent.putExtra(Config.EXTRA_CONFIG, config)
                } else {
                    intent = Intent(activity, CameraActivty::class.java)
                    intent.putExtra(Config.EXTRA_CONFIG, config)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }
                return intent
            }

    }

    internal class FragmentBuilder(private val fragment: Fragment) :
        Builder(fragment) {
        override fun start() {
            val intent = intent!!
            val requestCode: Int =
                if (config.requestCode !== 0) config.requestCode else Config.RC_PICK_IMAGES
            if (!config.isCameraOnly) {
                fragment.startActivityForResult(intent, requestCode)
            } else {
                fragment.activity!!.overridePendingTransition(0, 0)
                fragment.startActivityForResult(intent, requestCode)
            }
        }

        override val intent: Intent?
            get() {
                val intent: Intent
                if (!config.isCameraOnly) {
                    intent = Intent(fragment.activity, ImagePickerActivity::class.java)
                    intent.putExtra(Config.EXTRA_CONFIG, config)
                } else {
                    intent = Intent(fragment.activity, CameraActivty::class.java)
                    intent.putExtra(Config.EXTRA_CONFIG, config)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }
                return intent
            }

    }

    abstract class Builder : BaseBuilder {
        constructor(activity: Activity?) : super(activity)
        constructor(fragment: Fragment) : super(fragment.context)

        fun setToolbarColor(toolbarColor: String?): Builder {
            config.setToolbarColor(toolbarColor)
            return this
        }

        fun setStatusBarColor(statusBarColor: String?): Builder {
            config.setStatusBarColor(statusBarColor)
            return this
        }

        fun setToolbarTextColor(toolbarTextColor: String?): Builder {
            config.setToolbarTextColor(toolbarTextColor)
            return this
        }

        fun setToolbarIconColor(toolbarIconColor: String?): Builder {
            config.setToolbarIconColor(toolbarIconColor)
            return this
        }

        fun setProgressBarColor(progressBarColor: String?): Builder {
            config.setProgressBarColor(progressBarColor)
            return this
        }

        fun setBackgroundColor(backgroundColor: String?): Builder {
            config.setBackgroundColor(backgroundColor)
            return this
        }

        fun setCameraOnly(isCameraOnly: Boolean): Builder {
            config.isCameraOnly = isCameraOnly
            return this
        }

        fun setMultipleMode(isMultipleMode: Boolean): Builder {
            config.isMultipleMode = isMultipleMode
            return this
        }

        fun setFolderMode(isFolderMode: Boolean): Builder {
            config.isFolderMode = isFolderMode
            return this
        }

        fun setShowCamera(isShowCamera: Boolean): Builder {
            config.isShowCamera = isShowCamera
            return this
        }

        fun setMaxSize(maxSize: Int): Builder {
            config.maxSize = maxSize
            return this
        }

        fun setDoneTitle(doneTitle: String?): Builder {
            config.doneTitle = doneTitle
            return this
        }

        fun setFolderTitle(folderTitle: String?): Builder {
            config.folderTitle = folderTitle
            return this
        }

        fun setImageTitle(imageTitle: String?): Builder {
            config.imageTitle = imageTitle
            return this
        }

        fun setLimitMessage(message: String?): Builder {
            config.limitMessage = message
            return this
        }

        fun setSavePath(path: String?): Builder {
            config.savePath = SavePath(path!!, false)
            return this
        }

        fun setAlwaysShowDoneButton(isAlwaysShowDoneButton: Boolean): Builder {
            config.isAlwaysShowDoneButton = isAlwaysShowDoneButton
            return this
        }

        fun setKeepScreenOn(keepScreenOn: Boolean): Builder {
            config.isKeepScreenOn = keepScreenOn
            return this
        }

        fun setSelectedImages(selectedImages: ArrayList<Image>): Builder {
            config.selectedImages = selectedImages
            return this
        }

        fun setRequestCode(requestCode: Int): Builder {
            config.requestCode = requestCode
            return this
        }

        abstract fun start()
        abstract val intent: Intent?
    }

    abstract class BaseBuilder(context: Context?) {
        var config: Config

        init {
            config = Config()
            val resources = context!!.resources
            config.isCameraOnly = false
            config.isMultipleMode = true
            config.isFolderMode = true
            config.isShowCamera = config.isShowCamera
            config.maxSize = Config.MAX_SIZE
            config.doneTitle = resources.getString(R.string.action_done)
            config.folderTitle = resources.getString(R.string.title_folder)
            config.imageTitle = resources.getString(R.string.title_image)
            config.limitMessage = resources.getString(R.string.msg_limit_images)
            config.savePath = SavePath.DEFAULT
            config.isAlwaysShowDoneButton = false
            config.isKeepScreenOn = false
            config.selectedImages = ArrayList<Image>()
        }
    }

    companion object {
        fun with(activity: Activity): Builder {
            return ActivityBuilder(activity)
        }

        fun with(fragment: Fragment): Builder {
            return FragmentBuilder(fragment)
        }
    }

    init {
        config = builder.config
    }
}


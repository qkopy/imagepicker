package com.museon.gallerydemo


import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.qkopy.gallery.model.Config
import com.qkopy.gallery.model.Config.Companion.RC_PICK_IMAGES
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.ui.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private var images = ArrayList<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            startFoldersList()
        }
        btn.setOnClickListener {
            startImagesList()
        }

        btnTest.setOnClickListener {
            openImagePicker()
        }
    }

    fun openImagePicker() {
        val images = ArrayList<Image>()
        var selectImageCount =
            4//postViewModel.remoteConfig.getLong(REMOTE_CONFIG_MAX_IMAGE_SIZE).toInt()

        if (selectImageCount == 0) {
            selectImageCount = 1
        }

        ImagePicker.with(this)
            .setFolderMode(true)
            .setCameraOnly(false)
            .setFolderTitle("album")
            .setShowCamera(true)
            .setMultipleMode(true)
            .setIsCropEnabled(true)
            .setSelectedImages(images)
            .setMaxSize(selectImageCount)
            .setBackgroundColor("#212121")
            .setAlwaysShowDoneButton(false)
            .setRequestCode(100)
            .setKeepScreenOn(true)
            .start()
    }

    private fun startFoldersList() {


        ImagePicker.with(this)
            .setFolderMode(true)
            .setCameraOnly(false)
            .setFolderTitle("Album")
            .setShowCamera(true)
            .setMultipleMode(true)
            .setSelectedImages(images)
            .setMaxSize(10)
            .setBackgroundColor("#212121")
            .setAlwaysShowDoneButton(false)
            .setRequestCode(100)
            .setKeepScreenOn(true)
            .start()

    }

    private fun startImagesList() {


        ImagePicker.with(this)
            .setFolderMode(false)
            .setCameraOnly(false)
            .setFolderTitle("Album")
            .setShowCamera(true)
            .setMultipleMode(true)
            .setSelectedImages(images)
            .setMaxSize(10)
            .setBackgroundColor("#212121")
            .setAlwaysShowDoneButton(false)
            .setRequestCode(100)
            .setKeepScreenOn(true)
            .start()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // image picker
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)
            imageview.setImageURI(Uri.fromFile(File(images[0].path)))
            images.forEach {
                Log.d("Image:",it.path)
            }
            //adapter!!.setData(images)
        }
    }
}

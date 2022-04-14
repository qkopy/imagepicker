package com.museon.gallerydemo


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.qkopy.gallery.model.Config
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
            1//postViewModel.remoteConfig.getLong(REMOTE_CONFIG_MAX_IMAGE_SIZE).toInt()

        if (selectImageCount == 0) {
            selectImageCount = 1
        }

        ImagePicker.with(this)
            .setFolderMode(true)
            .setCameraOnly(false)
            .setFolderTitle("album")
            .setShowCamera(true)
            .setMultipleMode(false)
            .setIsCropEnabled(true)
            //.setIsCropMandatory(true)
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
            .setMultipleMode(false)
            .setSelectedImages(images)
            .setMaxSize(1)
            .setIsCropEnabled(true)
            .setIsCropMandatory(true)
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

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_TITLE, "img.jpg")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, 112)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 112 && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                val f = File(images[0].path)
                val dat = f.bufferedReader()
                val out = contentResolver.openOutputStream(uri)

                out?.bufferedWriter()?.let { dat.copyTo(it) }
                out?.close()

            }
        }
        // image picker
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            images =
                data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES) as? ArrayList<Image>
                    ?: arrayListOf()
            val imageFile = File(images[0].path)
            createFile()
            imageview.setImageURI(Uri.fromFile(imageFile))
//            val bitmap = BitmapFactory.decodeFile(imageFile.path)
//            val palette = Palette.from(bitmap).setRegion(0,0,bitmap.width/2,bitmap.height/2)
//                .generate()
//            val dark = palette.getDarkMutedColor(ContextCompat.getColor(this,R.color.black))
//            val dominant = palette.getDominantColor(ContextCompat.getColor(this,R.color.black))
//            val vibrant = palette.getVibrantColor(ContextCompat.getColor(this,R.color.black))
//            val lightm = palette.getLightMutedColor(ContextCompat.getColor(this,R.color.black))
//            val muted = palette.getMutedColor(ContextCompat.getColor(this,R.color.black))
//            color1.setBackgroundColor(muted)
//            color2.setBackgroundColor(dominant)
//            color3.setBackgroundColor(vibrant)
//            color4.setBackgroundColor(lightm)
            images.forEach {
                Log.d("Image:", it.path)
            }
            //adapter!!.setData(images)
        }
    }
}

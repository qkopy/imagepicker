package com.museon.gallerydemo


import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.qkopy.gallery.model.Config
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.ui.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private var images = ArrayList<Image>()

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            images =
                it.data!!.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES) as? ArrayList<Image>
                    ?: arrayListOf()
            val imageFile = File(images[0].path)
            //createFile()
            imageview.setImageURI(Uri.fromFile(imageFile))
            images.forEach {
                Log.d("Image:", it.path)
            }
        }
    }

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
        btn_test.setOnClickListener {
            ImagePicker.with(this)
                .setFolderMode(true)
                .setCameraOnly(false)
                .setFolderTitle("album")
                .setShowCamera(true)
                .setMultipleMode(false)
                .setIsCropEnabled(false)
                //.setIsCropMandatory(true)
                .setMaxSize(1)
                .setBackgroundColor("#212121")
                .setAlwaysShowDoneButton(false)
                .setRequestCode(987)
                .setKeepScreenOn(true)
                .start()
        }
    }

    fun openImagePicker() {
        val images = ArrayList<Image>()
        var selectImageCount =
            1//postViewModel.remoteConfig.getLong(REMOTE_CONFIG_MAX_IMAGE_SIZE).toInt()

        if (selectImageCount == 0) {
            selectImageCount = 1
        }

        val pickerIntent = ImagePicker.with(this)
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
            .intent
            //.start()
        imagePickerLauncher.launch(pickerIntent)
    }

    private fun startFoldersList() {


        val pickerIntent = ImagePicker.with(this)
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
            .intent
           // .start()
        imagePickerLauncher.launch(pickerIntent)

    }

    private fun startImagesList() {


        val pickerIntent = ImagePicker.with(this)
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
            .intent
           // .start()
        imagePickerLauncher.launch(pickerIntent)

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

        if (requestCode == 987&& resultCode == Activity.RESULT_OK && data != null) {
            images =
                data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES) as? ArrayList<Image>
                    ?: arrayListOf()
           // val imageFile = File(images[0].path)
           // createFile()
            val opt = BitmapFactory.Options()
            opt.inJustDecodeBounds = true
            BitmapFactory.decodeFile(images[0].path, opt)
            val inSample = calculateInSampleSize(opt, 300, 300)
            opt.inSampleSize = inSample
            opt.inJustDecodeBounds = false
            val bmap = BitmapFactory.decodeFile(images[0].path, opt)
            Log.d("BMAP::","${bmap?.width}x${bmap?.height}")
            //val tempFile = File.createTempFile("test"+"_comp", ".jpg")

            val view = RelativeLayout(this)
            val mInflater = LayoutInflater.from(this)
            mInflater.inflate(R.layout.test_img_ayout, view, true)
            view.layoutParams = RelativeLayout.LayoutParams(
                300,
                300
            )

            //Pre-measure the view so that height and width don't remain null.
            view.measure(view.layoutParams.width, view.layoutParams.height)
            view.findViewById<ImageView>(R.id.img).setImageURI(Uri.fromFile(File(images[0].path)))
            //Assign a size and position to the view and all of its descendants

            //Assign a size and position to the view and all of its descendants
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            val bmapN = getBitmapFromView(view)
            imageview.setImageBitmap(bmapN)
            Log.d("BMAPN::","${bmapN?.width}x${bmapN?.height}")

        }
    }

    fun getBitmapFromView(view: View): Bitmap? {
        //Create the bitmap
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        //Create a canvas with the specified bitmap to draw into
        val c = Canvas(bitmap)
        //Render this view (and all of its children) to the given Canvas
        view.draw(c)
        return bitmap
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}

package com.qkopy.gallery.ui.imagepicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.qkopy.gallery.R
import com.qkopy.gallery.adapter.ImageCropAdapter
import com.qkopy.gallery.model.Config
import com.qkopy.gallery.model.Image
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_image_picker_final.*
import kotlinx.android.synthetic.main.activity_image_picker_final.toolbar
import kotlinx.android.synthetic.main.imagepicker_activity_picker.*
import java.io.File

class ImagePickerFinalActivity : AppCompatActivity(),ImageCropAdapter.CropListener {
    private var image:Image? = null
    lateinit var imageCropAdapter: ImageCropAdapter
    private val doneClickListener = View.OnClickListener { onDone() }
    private lateinit var config: Config
    private fun onDone() {
        val data = Intent()
        data.putParcelableArrayListExtra(Config.EXTRA_IMAGES, imageCropAdapter.images as ArrayList<out Parcelable>)
        setResult(Activity.RESULT_OK,data)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker_final)
        config = intent.getParcelableExtra(Config.EXTRA_CONFIG)
        setupToolbar()
        val images = intent.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
        images?.let {
            imageCropAdapter = ImageCropAdapter(this@ImagePickerFinalActivity,it,
                this@ImagePickerFinalActivity)
            val layoutmngr = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)

            recyclerViewImages.apply {
                layoutManager = layoutmngr
                adapter = imageCropAdapter
            }
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(recyclerViewImages)

            next.setOnClickListener { next() }
            previous.setOnClickListener { previous() }
        }

    }

    private fun setupToolbar() {
        toolbar.let { imagePickerToolbar ->
            config.let { imagePickerToolbar.config(it) }
            imagePickerToolbar.showOnlyDoneButton(true)
            imagePickerToolbar.setOnDoneClickListener(doneClickListener)
        }
    }

    private fun next(){
        val llayout = recyclerViewImages.layoutManager as LinearLayoutManager
        recyclerViewImages.smoothScrollToPosition(llayout.findLastVisibleItemPosition()+1)
    }
    private fun previous(){
        val llayout = recyclerViewImages.layoutManager as LinearLayoutManager
        recyclerViewImages.smoothScrollToPosition(llayout.findFirstVisibleItemPosition()-1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK){
            val outputUri = data?.let { UCrop.getOutput(it) }

            image?.let {
                outputUri?.let {uri-> it.path = uri.path }
                imageCropAdapter.updateItem(it)

            }
        }
    }

    override fun onClickCrop(image: Image) {
        val imgFile = File(image.path)
        val img = image.name.split(".")[0]
        val ext = image.name.split(".")[1]
        UCrop.of(Uri.fromFile(imgFile), Uri.fromFile(File.createTempFile(img,".$ext")))
            .withAspectRatio(1f,1f)
            .start(this)
        this.image = image
    }

    override fun onClickClose(image: Image) {
       imageCropAdapter.removeImage(image)
    }
}
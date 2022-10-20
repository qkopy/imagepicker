package com.qkopy.gallery.adapter

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qkopy.gallery.R
import com.qkopy.gallery.databinding.ImagepickerLayoutRecyclerviewItemCropBinding
import com.qkopy.gallery.model.Image
import com.yalantis.ucrop.UCrop
import java.io.File

class ImageCropAdapter(val activity: Activity,val images:ArrayList<Image>, val listener:CropListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       val view =  ImagepickerLayoutRecyclerviewItemCropBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ).root
        return ImageCropHolder(view)

    }
    interface CropListener{
        fun onClickCrop(image: Image)
        fun onClickClose(image: Image)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ImageCropHolder).bind(images[position],activity,listener)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun updateItem(image: Image){
        if (images.any { it.id == image.id }){
            val removeItem = images.single { it.id == image.id }
            val removeIndex = images.indexOf(removeItem)
            images.set(removeIndex,image)
            notifyItemChanged(removeIndex)
        }
    }

    fun removeImage(image: Image){
        val index = images.indexOf(image)
        images.removeAt(index)
        notifyItemRemoved(index)
    }

    class ImageCropHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        private val binding = ImagepickerLayoutRecyclerviewItemCropBinding.bind(itemView)
        fun bind(image: Image,activity: Activity,listener: CropListener){

//            itemView.imgCrop.setImageUri(
//                Uri.fromFile(File(image.path)),
//                Uri.fromFile(File.createTempFile(img,".$ext"))
//            )
            val imgFile = File(image.path)
          //  itemView.imgCrop.setImageURI(Uri.fromFile(imgFile))
            Glide.with(activity)
                .load(imgFile)
                .into(binding.imgCrop)
            binding.btnCrop.setOnClickListener {
                listener.onClickCrop(image)
            }
            binding.btnClose.setOnClickListener{
                listener.onClickClose(image)
            }
        }
    }

}
package com.qkopy.gallery.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.qkopy.gallery.R
import com.qkopy.gallery.databinding.ImagepickerItemImageBinding
import com.qkopy.gallery.helper.ImageHelper
import com.qkopy.gallery.listener.OnImageClickListener
import com.qkopy.gallery.listener.OnImageSelectionListener
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.ui.common.BaseRecyclerViewAdapter
import java.util.*


class ImagePickerAdapter(
    context: Context?,
    selectedImages: List<Image>?,
    private val itemClickListener: OnImageClickListener
) :
    BaseRecyclerViewAdapter<ImagePickerAdapter.ImageViewHolder>(context!!) {
    private val images: MutableList<Image> = ArrayList<Image>()
    private val selectedImages: MutableList<Image> = ArrayList<Image>()
    private var imageSelectionListener: OnImageSelectionListener? = null
    private var isSelection = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView: View =
            // inflater.inflate(R.layout.imagepicker_item_image, parent, false)
            ImagepickerItemImageBinding.inflate(inflater, parent, false).root
        return ImageViewHolder(itemView)
    }


    override fun onBindViewHolder(viewHolder: ImageViewHolder, position: Int) {
        val image: Image = images[position]
        val isSelected = isSelected(image)
        Glide.with(context).load(image.path)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .skipMemoryCache(true)
                    .centerCrop()
            )
            .into(viewHolder.image)
        viewHolder.gifIndicator.visibility =
            if (ImageHelper.isGifFormat(image)) View.VISIBLE else View.GONE
        viewHolder.alphaView.alpha = if (isSelected) 0.5f else 0.0f
        viewHolder.container.foreground = if (isSelected) ContextCompat.getDrawable(
            context,
            R.drawable.imagepicker_ic_selected
        ) else null
        viewHolder.itemView.setOnClickListener { view ->
            isSelection = true
            if (isSelection) {
                val shouldSelect = itemClickListener.onImageClick(
                    view,
                    viewHolder.adapterPosition,
                    !isSelected
                )
                if (isSelected) {
                    removeSelected(image, position)
                } else if (shouldSelect) {
                    addSelected(image, position)
                }
            }
            if (selectedImages.size == 0) {
                isSelection = false
            }
        }
        viewHolder.itemView.setOnLongClickListener { view ->
            if (!isSelected) {
                isSelection = true
                val shouldSelect = itemClickListener.onImageClick(
                    view,
                    viewHolder.adapterPosition,
                    !isSelected
                )
                if (isSelected) {
                    removeSelected(image, position)
                } else if (shouldSelect) {
                    addSelected(image, position)
                }
            }
            true
        }
    }

    private fun isSelected(image: Image): Boolean {
        for (selectedImage in selectedImages) {
            if (selectedImage.path.equals(image.path)) {
                return true
            }
        }
        return false
    }

    fun setOnImageSelectionListener(imageSelectedListener: OnImageSelectionListener?) {
        imageSelectionListener = imageSelectedListener
    }


    override fun getItemCount() = images.size

    fun setData(images: List<Image>?) {
        if (images != null) {
            this.images.clear()
            this.images.addAll(images)
        }
        notifyDataSetChanged()
    }

    fun addImage(image: Image) {
        if (!this.images.contains(image)) {
            this.images.add(image)
            notifyItemInserted(this.images.lastIndex)
        }

    }

    fun addSelected(images: List<Image>?) {
        selectedImages.addAll(images!!)
        notifySelectionChanged()
    }

    fun addSelected(image: Image, position: Int) {
        selectedImages.add(image)
        notifyItemChanged(position)
        notifySelectionChanged()
    }

    fun removeSelected(image: Image, position: Int) {
        if (selectedImages.size > 0) {
            for (selectedImage in selectedImages) {
                if (selectedImage.path.equals(image.path)) {
                    val index = selectedImages.indexOf(selectedImage)
                    selectedImages.removeAt(index)
                    break
                }
            }
        }
        notifyItemChanged(position)
        notifySelectionChanged()
    }

    fun removeAllSelected() {
        selectedImages.clear()
        notifyDataSetChanged()
        notifySelectionChanged()
    }

    private fun notifySelectionChanged() {
        if (imageSelectionListener != null) {
            imageSelectionListener!!.onSelectionUpdate(selectedImages)
        }
    }

    fun getSelectedImages(): List<Image> {
        return selectedImages
    }


    class ImageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val container: FrameLayout
        val image: ImageView
        val alphaView: View
        val gifIndicator: View

        init {
            val binding = ImagepickerItemImageBinding.bind(itemView)
            container = binding.root
            image = binding.imageThumbnail
            alphaView = binding.viewAlpha
            gifIndicator = binding.gifIndicator
        }
    }

    init {
        if (selectedImages != null && !selectedImages.isEmpty()) {
            isSelection = true
            this.selectedImages.addAll(selectedImages)
        }
    }
}

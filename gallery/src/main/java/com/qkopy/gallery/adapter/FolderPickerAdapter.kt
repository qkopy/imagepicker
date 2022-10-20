package com.qkopy.gallery.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.qkopy.gallery.R
import com.qkopy.gallery.databinding.ImagepickerItemFolderBinding
import com.qkopy.gallery.listener.OnFolderClickListener
import com.qkopy.gallery.model.Folder
import com.qkopy.gallery.ui.common.BaseRecyclerViewAdapter
import java.util.*

class FolderPickerAdapter(
    context: Context?,
    private val itemClickListener: OnFolderClickListener
) :
    BaseRecyclerViewAdapter<FolderPickerAdapter.FolderViewHolder?>(context!!) {
    private val folders: MutableList<Folder> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val itemView = ImagepickerItemFolderBinding.inflate(inflater, parent, false).root
        return FolderViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        val firstImagePath =
            if (folder.images?.isNotEmpty() == true) folder.images!![0].path else ""
        Glide.with(context)
            .load(firstImagePath)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            )
            .into(holder.image)
        holder.name.text = folder.folderName
        val count = folder.images!!.size
        holder.count.text = "" + count
        holder.itemView.setOnClickListener { itemClickListener.onFolderClick(folder) }
    }

    fun setData(folders: List<Folder>?) {
        if (folders != null) {
            this.folders.clear()
            this.folders.addAll(folders)
        }
        notifyDataSetChanged()
    }

    fun updateData(folder: Folder) {
        val index = folders.indexOf(folders.find { it.folderName.equals(folder.folderName) })
        //Log.e("INDEX:", index.toString())
        if (index == -1) {
            addData(folder)
            return
        }
        folders.set(index, folder)
        notifyItemChanged(index)
    }

    fun addData(folder: Folder) {
        this.folders.add(folder)
        notifyItemInserted(this.folders.size - 1)

    }


    class FolderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val image: ImageView
        val name: TextView
        val count: TextView

        init {
            val binding = ImagepickerItemFolderBinding.bind(itemView)
            image = binding.imageFolderThumbnail
            name = binding.textFolderName
            count = binding.textPhotoCount
        }
    }

    override fun getItemCount() = folders.size

}

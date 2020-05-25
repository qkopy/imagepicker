package com.qkopy.gallery.ui.imagepicker

import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qkopy.gallery.R
import com.qkopy.gallery.adapter.FolderPickerAdapter
import com.qkopy.gallery.adapter.ImagePickerAdapter
import com.qkopy.gallery.listener.OnBackAction
import com.qkopy.gallery.listener.OnFolderClickListener
import com.qkopy.gallery.listener.OnImageClickListener
import com.qkopy.gallery.listener.OnImageSelectionListener
import com.qkopy.gallery.model.Config
import com.qkopy.gallery.model.Folder
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.widget.GridSpacingItemDecoration

class RecyclerViewManager(
    private val recyclerView: RecyclerView,
    private val config: Config,
    orientation: Int
) {
    private val context: Context
    private var layoutManager: GridLayoutManager? = null
    private var itemOffsetDecoration: GridSpacingItemDecoration? = null
    private var imageAdapter: ImagePickerAdapter? = null
    private var folderAdapter: FolderPickerAdapter? = null
    private var imageColumns = 0
    private var folderColumns = 0
    private var foldersState: Parcelable? = null
    private var title: String? = null
    private var isShowingFolder: Boolean
    fun setupAdapters(
        imageClickListener: OnImageClickListener?,
        folderClickListener: OnFolderClickListener
    ) {
        var selectedImages: ArrayList<Image>? = null
        if (config.isMultipleMode && !config.selectedImages!!.isEmpty()) {
            selectedImages = config.selectedImages
        }
        imageAdapter = ImagePickerAdapter(context, selectedImages, imageClickListener!!)
        folderAdapter = FolderPickerAdapter(context, object : OnFolderClickListener {
            override fun onFolderClick(folder: Folder) {
                foldersState = recyclerView.layoutManager!!.onSaveInstanceState()
                folderClickListener.onFolderClick(folder)
            }
        })
    }

    /**
     * Set item size, column size base on the screen orientation
     */
    fun changeOrientation(orientation: Int) {
        imageColumns =
            if (orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 5
        folderColumns =
            if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4
        val columns = if (isShowingFolder) folderColumns else imageColumns
        layoutManager = GridLayoutManager(context, columns)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        setItemDecoration(columns)
    }

    private fun setItemDecoration(columns: Int) {
        if (itemOffsetDecoration != null) {
            recyclerView.removeItemDecoration(itemOffsetDecoration!!)
        }
        itemOffsetDecoration = GridSpacingItemDecoration(
            columns,
            context.resources.getDimensionPixelSize(R.dimen.item_padding),
            false
        )
        recyclerView.addItemDecoration(itemOffsetDecoration!!)
        layoutManager!!.spanCount = columns
    }

    fun setOnImageSelectionListener(imageSelectionListener: OnImageSelectionListener?) {
        checkAdapterIsInitialized()
        imageAdapter!!.setOnImageSelectionListener(imageSelectionListener)
    }

    val selectedImages: List<Image>
        get() {
            checkAdapterIsInitialized()
            return imageAdapter!!.getSelectedImages()
        }

    fun addSelectedImages(images: List<Image>?) {
        imageAdapter!!.addSelected(images)
    }

    private fun checkAdapterIsInitialized() {
        checkNotNull(imageAdapter) { "Must call setupAdapters first!" }
    }

    fun selectImage(): Boolean {
        if (config.isMultipleMode) {
            if (imageAdapter!!.getSelectedImages().size >= config.maxSize) {
                val message =
                    String.format(config.limitMessage!!, config.maxSize)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                return false
            }
        } else {
            if (imageAdapter!!.itemCount > 0) {
                imageAdapter!!.removeAllSelected()
            }
        }
        return true
    }

    fun handleBack(action: OnBackAction) {
        if (config.isFolderMode && !isShowingFolder) {
            setFolderAdapter(null)
            action.onBackToFolder()
            return
        }
        action.onFinishImagePicker()
    }

    fun setImageAdapter(
        images: List<Image>?,
        title: String?
    ) {
        imageAdapter!!.setData(images)
        setItemDecoration(imageColumns)
        recyclerView.setAdapter(imageAdapter)
        this.title = title
        isShowingFolder = false
    }

    fun setFolderAdapter(folders: List<Folder>?) {
        folderAdapter!!.setData(folders)
        setItemDecoration(folderColumns)
        recyclerView.setAdapter(folderAdapter)
        isShowingFolder = true
        if (foldersState != null) {
            layoutManager!!.spanCount = folderColumns
            recyclerView.layoutManager!!.onRestoreInstanceState(foldersState)
        }
    }

    fun getTitle(): String? {
        return if (isShowingFolder) {
            config.folderTitle
        } else if (config.isFolderMode) {
            title
        } else {
            config.imageTitle
        }
    }

    val isShowDoneButton: Boolean
        get() = config.isMultipleMode && (config.isAlwaysShowDoneButton || imageAdapter!!.getSelectedImages().size > 0)

    fun selectedMediaCount(): Int {
        return imageAdapter!!.getSelectedImages().size
    }

    init {
        context = recyclerView.context
        changeOrientation(orientation)
        isShowingFolder = config.isFolderMode
    }
}

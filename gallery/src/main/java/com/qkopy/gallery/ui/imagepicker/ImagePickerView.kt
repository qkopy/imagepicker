package com.qkopy.gallery.ui.imagepicker

import com.qkopy.gallery.model.Folder
import com.qkopy.gallery.model.Image
import com.qkopy.gallery.ui.common.MvpView

interface ImagePickerView : MvpView {
    fun showLoading(isLoading: Boolean)
    fun showFetchCompleted(
        images: List<Image>,
        folders: List<Folder>
    )

    fun showError(throwable: Throwable?)
    fun showEmpty()
    fun showCapturedImage(images: List<Image>?)
    fun finishPickImages(images: List<Image>?)
    fun showFetching(images: List<Image>?,folders: List<Folder>?)
    fun showUpdateFolder(folder: Folder)
    fun showUpdateImage(images: List<Image>?)
}
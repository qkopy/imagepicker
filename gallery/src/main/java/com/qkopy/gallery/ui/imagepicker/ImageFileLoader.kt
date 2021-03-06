package com.qkopy.gallery.ui.imagepicker

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.qkopy.gallery.listener.OnImageLoaderListener
import com.qkopy.gallery.model.Folder
import com.qkopy.gallery.model.Image
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class ImageFileLoader(private val context: Context) {
    private val projection = arrayOf(
        MediaStore.Images.Media._ID
        , MediaStore.Images.Media.DISPLAY_NAME
        , MediaStore.Images.Media.DATA
        , MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )
    private var executorService: ExecutorService? = null
    fun loadDeviceImages(
        isFolderMode: Boolean,
        listener: OnImageLoaderListener?
    ) {
        getExecutorService()!!.execute(ImageLoadRunnable(isFolderMode, listener!!))
    }

    fun abortLoadImages() {
        if (executorService != null) {
            executorService!!.shutdown()
            executorService = null
        }
    }

    private fun getExecutorService(): ExecutorService? {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor()
        }
        return executorService
    }

    private inner class ImageLoadRunnable(
        private val isFolderMode: Boolean,
        private val listener: OnImageLoaderListener
    ) :
        Runnable {
        override fun run() {
            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, MediaStore.Images.Media.DATE_ADDED
            )
            if (cursor == null) {
                listener.onFailed(NullPointerException())
                return
            }
            val images: MutableList<Image> =
                ArrayList(cursor.count)
            val folderMap: MutableMap<String, Folder>? =
                if (isFolderMode) LinkedHashMap() else null
            if (cursor.moveToLast()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndex(projection[0]))
                    val name =
                        cursor.getString(cursor.getColumnIndex(projection[1]))
                    val path =
                        cursor.getString(cursor.getColumnIndex(projection[2]))
                    val bucket =
                        cursor.getString(cursor.getColumnIndex(projection[3]))
                    val file = makeSafeFile(path)
                    if (file != null && file.exists()) {
                        val image =
                            Image(id, name, path, false)
                        images.add(image)
                        if (folderMap != null) {
                            var folder = folderMap[bucket ?: "Other_Images_"]
                            if (folder == null) {
                                if (bucket==null){
                                    Log.e("BUCKETNULL:",image.name+"::"+
                                    image.path+"::")
                                }
                                folder = Folder(bucket ?: "")
                                folderMap[bucket ?: "Other_Images_"] = folder
                                listener.onFolderAdded(images, ArrayList(folderMap.values))
                                Log.d("FOLDERSIZE:",folderMap.values.size.toString())
                            }
                            folder.images!!.add(image)
                            if (isFolderMode) listener.onFolderUpdated(folder)

                        } else {
                            if (images.size == 1)
                                listener.onFolderAdded(images, ArrayList<Folder>())
                            else
                                listener.onImageAdded(image)
                        }


                    }


                } while (cursor.moveToPrevious())
            }
            cursor.close()

            if ((folderMap != null && folderMap.values.isEmpty()) || images.isEmpty()) {
                listener.onEmpty()
            }
        }

    }

    companion object {
        private fun makeSafeFile(path: String?): File? {
            return if (path == null || path.isEmpty()) {
                null
            } else try {
                File(path)
            } catch (ignored: Exception) {
                null
            }
        }
    }

}

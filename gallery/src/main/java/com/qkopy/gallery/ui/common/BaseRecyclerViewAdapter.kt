package com.qkopy.gallery.ui.common

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T : RecyclerView.ViewHolder?>(val context: Context) :
    RecyclerView.Adapter<T>() {
    val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }
}

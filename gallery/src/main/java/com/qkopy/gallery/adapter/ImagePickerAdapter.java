/*
 * Created by @ajithvgiri on 7/12/18 10:05 AM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.qkopy.gallery.R;
import com.qkopy.gallery.helper.ImageHelper;
import com.qkopy.gallery.listener.OnImageClickListener;
import com.qkopy.gallery.listener.OnImageSelectionListener;
import com.qkopy.gallery.model.Image;
import com.qkopy.gallery.ui.common.BaseRecyclerViewAdapter;
import com.qkopy.gallery.widget.GlideApp;

import java.util.ArrayList;
import java.util.List;


public class ImagePickerAdapter extends BaseRecyclerViewAdapter<ImagePickerAdapter.ImageViewHolder> {

    private List<Image> images = new ArrayList<>();
    private List<Image> selectedImages = new ArrayList<>();
    private OnImageClickListener itemClickListener;
    private OnImageSelectionListener imageSelectionListener;
    private Boolean isSelection = false;

    public ImagePickerAdapter(Context context, List<Image> selectedImages, OnImageClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;

        if (selectedImages != null && !selectedImages.isEmpty()) {
            isSelection = true;
            this.selectedImages.addAll(selectedImages);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = getInflater().inflate(R.layout.imagepicker_item_image, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder viewHolder, final int position) {

        final Image image = images.get(position);
        final boolean isSelected = isSelected(image);

        GlideApp.with(getContext()).
                load(image.getPath()).
                centerCrop().
                transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.image);

        viewHolder.gifIndicator.setVisibility(ImageHelper.isGifFormat(image) ? View.VISIBLE : View.GONE);
        viewHolder.alphaView.setAlpha(isSelected ? 0.5f : 0.0f);
        viewHolder.container.setForeground(isSelected
                ? ContextCompat.getDrawable(getContext(), R.drawable.imagepicker_ic_selected)
                : null);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelection = true;
                if (isSelection) {
                    boolean shouldSelect = itemClickListener.onImageClick(view, viewHolder.getAdapterPosition(), !isSelected);
                    if (isSelected) {
                        removeSelected(image, position);
                    } else if (shouldSelect) {
                        addSelected(image, position);
                    }
                }

                if (selectedImages.size() == 0) {
                    isSelection = false;
                }

            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isSelected) {
                    isSelection = true;
                    boolean shouldSelect = itemClickListener.onImageClick(view, viewHolder.getAdapterPosition(), !isSelected);
                    if (isSelected) {
                        removeSelected(image, position);
                    } else if (shouldSelect) {
                        addSelected(image, position);
                    }
                }
                return true;
            }
        });
    }

    private boolean isSelected(Image image) {
        for (Image selectedImage : selectedImages) {
            if (selectedImage.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    public void setOnImageSelectionListener(OnImageSelectionListener imageSelectedListener) {
        this.imageSelectionListener = imageSelectedListener;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public void setData(List<Image> images) {
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
        notifyDataSetChanged();
    }

    public void addSelected(List<Image> images) {
        selectedImages.addAll(images);
        notifySelectionChanged();
    }

    public void addSelected(Image image, int position) {
        selectedImages.add(image);
        notifyItemChanged(position);
        notifySelectionChanged();
    }

    public void removeSelected(Image image, int position) {
        if (selectedImages.size() > 0) {
            for (Image selectedImage : selectedImages) {
                if (selectedImage.getPath().equals(image.getPath())) {
                    int index = selectedImages.indexOf(selectedImage);
                    selectedImages.remove(index);
                    break;
                }
            }
        }
        notifyItemChanged(position);
        notifySelectionChanged();
    }

    public void removeAllSelected() {
        selectedImages.clear();
        notifyDataSetChanged();
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        if (imageSelectionListener != null) {
            imageSelectionListener.onSelectionUpdate(selectedImages);
        }
    }

    public List<Image> getSelectedImages() {
        return selectedImages;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout container;
        private ImageView image;
        private View alphaView;
        private View gifIndicator;

        public ImageViewHolder(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView;
            image = itemView.findViewById(R.id.image_thumbnail);
            alphaView = itemView.findViewById(R.id.view_alpha);
            gifIndicator = itemView.findViewById(R.id.gif_indicator);

        }

    }

}

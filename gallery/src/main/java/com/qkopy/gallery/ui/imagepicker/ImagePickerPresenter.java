/*
 * Created by @ajithvgiri on 4/12/18 8:14 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.ui.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.qkopy.gallery.R;
import com.qkopy.gallery.listener.OnImageLoaderListener;
import com.qkopy.gallery.model.Config;
import com.qkopy.gallery.model.Folder;
import com.qkopy.gallery.model.Image;
import com.qkopy.gallery.ui.camera.CameraModule;
import com.qkopy.gallery.ui.camera.DefaultCameraModule;
import com.qkopy.gallery.ui.camera.OnImageReadyListener;
import com.qkopy.gallery.ui.common.BasePresenter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class ImagePickerPresenter extends BasePresenter<ImagePickerView> {

    private ImageFileLoader imageLoader;
    private CameraModule cameraModule = new DefaultCameraModule();
    private Handler handler = new Handler(Looper.getMainLooper());

    public ImagePickerPresenter(ImageFileLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void abortLoading() {
        imageLoader.abortLoadImages();
    }

    public void loadImages(boolean isFolderMode) {
        if (!isViewAttached()) return;


        getView().showLoading(true);
        imageLoader.loadDeviceImages(isFolderMode, new OnImageLoaderListener() {

            @Override
            public void onImageLoaded(@NotNull final List<? extends Image> images, @NotNull final List<Folder> folders) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewAttached()) {
                            getView().showFetchCompleted((List<Image>) images, folders);
                            final boolean isEmpty = folders != null ? folders.isEmpty() : images.isEmpty();
                            if (isEmpty) {
                                getView().showEmpty();
                            } else {
                                getView().showLoading(false);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailed(final Throwable throwable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewAttached()) {
                            getView().showError(throwable);
                        }
                    }
                });
            }
        });
    }

    void captureImage(Activity activity, Config config, int requestCode) {
        Context context = activity.getApplicationContext();
        Intent intent = cameraModule.getCameraIntent(activity, config);
        if (intent == null) {
            Toast.makeText(context, context.getString(R.string.error_create_image_file), Toast.LENGTH_LONG).show();
            return;
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public void finishCaptureImage(Context context, Intent data, final Config config) {
        cameraModule.getImage(context, data, new OnImageReadyListener() {
            @Override
            public void onImageReady(List<Image> images) {
                if (!config.isMultipleMode()) {
                    getView().finishPickImages(images);
                } else {
                    //disable this
                    getView().finishPickImages(images);
                    //getView().showCapturedImage(images);
                }
            }
        });
    }

    public void onDoneSelectImages(List<Image> selectedImages) {
        if (selectedImages != null && !selectedImages.isEmpty()) {
            for (int i = 0; i < selectedImages.size(); i++) {
                Image image = selectedImages.get(i);
                File file = new File(image.getPath());
                if (!file.exists()) {
                    selectedImages.remove(i);
                    i--;
                }
            }
        }
        getView().finishPickImages(selectedImages);
    }

}

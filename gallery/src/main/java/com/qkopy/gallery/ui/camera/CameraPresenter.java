/*
 * Created by @ajithvgiri on 4/12/18 8:11 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.ui.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.qkopy.gallery.R;
import com.qkopy.gallery.model.Config;
import com.qkopy.gallery.model.Image;
import com.qkopy.gallery.ui.common.BasePresenter;

import java.util.List;

public class CameraPresenter extends BasePresenter<CameraView> {

    private CameraModule cameraModule = new DefaultCameraModule();

    public CameraPresenter() {
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
                getView().finishPickImages(images);
            }
        });
    }
}

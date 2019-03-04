/*
 * Created by @ajithvgiri on 4/12/18 8:11 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.ui.camera;

import android.content.Context;
import android.content.Intent;
import com.qkopy.gallery.model.Config;

public interface CameraModule {
    Intent getCameraIntent(Context context, Config config);

    void getImage(Context context, Intent intent, OnImageReadyListener imageReadyListener);
}

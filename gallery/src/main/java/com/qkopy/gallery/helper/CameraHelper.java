/*
 * Created by @ajithvgiri on 4/12/18 8:01 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.helper;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Toast;
import com.qkopy.gallery.R;

public class CameraHelper {

    public static boolean checkCameraAvailability(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean isAvailable = intent.resolveActivity(context.getPackageManager()) != null;

        if (!isAvailable) {
            Context appContext = context.getApplicationContext();
            Toast.makeText(appContext,
                    appContext.getString(R.string.error_no_camera), Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }
}

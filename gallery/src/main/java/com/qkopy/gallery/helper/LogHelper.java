/*
 * Created by @ajithvgiri on 4/12/18 8:01 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.helper;

import android.util.Log;

public class LogHelper {

    private static final String TAG = "Gallery";

    private static LogHelper INSTANCE;

    private boolean isEnable = true;

    private LogHelper() {
    }

    public static LogHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LogHelper();
        }
        return INSTANCE;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void d(String message) {
        if (isEnable) {
            Log.d(TAG, message);
        }
    }

    public void e(String message) {
        if (isEnable) {
            Log.e(TAG, message);
        }
    }

    public void w(String message) {
        if (isEnable) {
            Log.w(TAG, message);
        }
    }
}

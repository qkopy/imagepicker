/*
 * Created by @ajithvgiri on 4/12/18 8:07 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.qkopy.gallery.BuildConfig;

public class SavePath implements Parcelable {

    public static final SavePath DEFAULT = new SavePath(BuildConfig.IMAGE_PATH, false);

    public static final Creator<SavePath> CREATOR = new Creator<SavePath>() {
        @Override
        public SavePath createFromParcel(Parcel source) {
            return new SavePath(source);
        }

        @Override
        public SavePath[] newArray(int size) {
            return new SavePath[size];
        }
    };
    private final String path;
    private final boolean isFullPath;

    public SavePath(String path, boolean isFullPath) {
        this.path = path;
        this.isFullPath = isFullPath;
    }

    protected SavePath(Parcel in) {
        this.path = in.readString();
        this.isFullPath = in.readByte() != 0;
    }

    public String getPath() {
        return path;
    }

    public boolean isFullPath() {
        return isFullPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeByte(this.isFullPath ? (byte) 1 : (byte) 0);
    }
}

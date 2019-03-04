/*
 * Created by @ajithvgiri on 18/12/18 3:58 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 18/12/18 3:58 PM
 */

package com.qkopy.gallery.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    private long id;
    private String name;
    private String path;
    private Boolean isSelected = false;

//    public Image(long id, String name, String path) {
//        this.id = id;
//        this.name = name;
//        this.path = path;
//    }

    public Image(long id, String name, String path, Boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
    }

    protected Image(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.path = in.readString();
        boolean[] myBooleanArr = new boolean[1];
        in.readBooleanArray(myBooleanArr);
        this.isSelected = myBooleanArr[0];
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeBooleanArray(new boolean[]{this.isSelected});
    }
}
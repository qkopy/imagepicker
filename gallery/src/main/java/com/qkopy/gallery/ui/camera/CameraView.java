/*
 * Created by @ajithvgiri on 4/12/18 8:11 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.ui.camera;

import com.qkopy.gallery.model.Image;
import com.qkopy.gallery.ui.common.MvpView;

import java.util.List;

public interface CameraView extends MvpView {

    void finishPickImages(List<Image> images);
}

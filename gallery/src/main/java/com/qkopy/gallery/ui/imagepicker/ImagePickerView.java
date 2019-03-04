/*
 * Created by @ajithvgiri on 4/12/18 8:14 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 4/12/18 7:05 PM
 */

package com.qkopy.gallery.ui.imagepicker;

import com.qkopy.gallery.model.Folder;
import com.qkopy.gallery.model.Image;
import com.qkopy.gallery.ui.common.MvpView;

import java.util.List;

public interface ImagePickerView extends MvpView {

    void showLoading(boolean isLoading);

    void showFetchCompleted(List<Image> images, List<Folder> folders);

    void showError(Throwable throwable);

    void showEmpty();

    void showCapturedImage(List<Image> images);

    void finishPickImages(List<Image> images);

}
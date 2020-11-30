package com.ilya.photomap.ui.screens.main;

import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.ui.base.BaseView;
import com.ilya.photomap.ui.base.InfoView;
import com.ilya.photomap.ui.base.RequestPermissionView;

import java.util.List;

import io.reactivex.Single;

public interface MainView extends BaseView, InfoView, RequestPermissionView {

    void openAuthorization();
    void openCamera();
    void notifyPhotosUpdated();
    void deletePhoto(int id, int position);
    void notifyPhotoDeleted(int id, int position);

}

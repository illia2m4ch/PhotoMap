package com.ilya.photomap.ui.screens.main.photos;

import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.data.network.model.PhotoOutDTO;
import com.ilya.photomap.ui.base.BaseView;
import com.ilya.photomap.ui.base.InfoView;
import com.ilya.photomap.ui.base.RequestPermissionView;

import java.util.List;

import io.reactivex.Single;

public interface PhotosView extends BaseView, InfoView {

    void displayPhotos(List<Photo> photos, boolean isFirstPage);
    void refreshPhotos(boolean saveToDatabase);
    void deletePhoto(int position);

}

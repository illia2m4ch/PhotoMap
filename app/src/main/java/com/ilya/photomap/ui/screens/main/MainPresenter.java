package com.ilya.photomap.ui.screens.main;

import android.Manifest;
import android.content.Context;

import com.ilya.photomap.App;
import com.ilya.photomap.R;
import com.ilya.photomap.data.network.model.PhotoInDTO;
import com.ilya.photomap.repository.PhotoRepository;
import com.ilya.photomap.ui.base.BasePresenter;
import com.ilya.photomap.util.AppUtil;

import java.util.Calendar;

/**
 * Presenter for Main Activity
 */
public class MainPresenter<T extends MainView> extends BasePresenter<T> {

    private final PhotoRepository repository;

    public MainPresenter() {
        repository = new PhotoRepository();
    }

    public void checkCurrentToken() {
        String token = App.getToken();
        if (token == null) getView().openAuthorization();
    }

    public void tryOpenCamera(Context context, boolean isGpsEnabled) {
        if (!hasPermissions(context)) return;
        if (!isGpsEnabled) {
            getView().showMessage(R.string.enable_gps);
            return;
        }
        getView().openCamera();
    }

    private boolean hasPermissions(Context context) {
        boolean hasCameraPermission = AppUtil.hasPermission(context, Manifest.permission.CAMERA);

        if (!hasCameraPermission) {
            getView().requestPermission(Manifest.permission.CAMERA);
            return false;
        }

        boolean hasLocationPermission = AppUtil.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        if (!hasLocationPermission) {
            getView().requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            return false;
        }

        return true;
    }

    public void addPhoto(String base64Photo, double latitude, double longitude) {
        PhotoInDTO photoIn = new PhotoInDTO();
        photoIn.base64Photo = base64Photo;
        photoIn.date = Calendar.getInstance().getTimeInMillis() / 1000;
        photoIn.lat = latitude;
        photoIn.lng = longitude;

        getCompositeDisposable().add(repository.addPhoto(photoIn)
                .subscribe(responseBody -> {
                    if (!isViewAttached()) return;
                    getView().showMessage(R.string.photo_uploaded);
                    getView().notifyPhotosUpdated();
                }, throwable -> {
                    if (!isViewAttached()) return;

                    getView().showMessage(R.string.error);
                })
        );
    }

    public void deletePhoto(int id, int position) {
        getCompositeDisposable().add(repository.deletePhoto(id)
                .subscribe(() -> {
                    if (!isViewAttached()) return;

                    getView().notifyPhotoDeleted(id, position);
                    getView().showMessage(R.string.photo_deleted);
                }, throwable -> {
                    if (!isViewAttached()) return;

                    getView().showMessage(R.string.error);
                })
        );
    }

    public void logout() {
        App.logout();
        getView().openAuthorization();
    }

}

package com.ilya.photomap.ui.screens.main.map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ilya.photomap.R;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.repository.PhotoRepository;
import com.ilya.photomap.ui.base.BasePresenter;
import com.ilya.photomap.ui.base.UIState;
import com.ilya.photomap.util.DateUtil;
import com.ilya.photomap.util.ListUtil;

import java.util.List;

import io.reactivex.Single;

/**
 * Presenter for Map Fragment
 */
public class MapPresenter<T extends MapView> extends BasePresenter<T> {

    private final PhotoRepository repository;
    private int page;
    private boolean isLoading; // are photos loading right now
    private boolean hasMorePhotos;

    public MapPresenter() {
        repository = new PhotoRepository();
        page = 0;
        isLoading = false;
        hasMorePhotos = true;
    }

    public boolean hasMorePhotos() {
        return hasMorePhotos;
    }

    public void loadMarkers() {
        loadMarkers(true);
    }

    private void loadMarkers(boolean saveToDatabase) {
        if (isLoading) return;
        isLoading = true;

        getCompositeDisposable().add(repository.loadPhotos(page, saveToDatabase)
                .subscribe(photos -> {
                    if (!isViewAttached()) return;

                    getView().setUIState(UIState.CONTENT);
                    List<MarkerOptions> markers = ListUtil.map(photos, photo -> {
                        LatLng coordinates = new LatLng(photo.latitude, photo.longitude);
                        return new MarkerOptions()
                                .position(coordinates)
                                .title(DateUtil.getFormattedDate(photo.date));
                    });

                    if (photos.size() == 0) hasMorePhotos = false;
                    else {
                        page++;
                        getView().displayMarkers(markers);
                    }
                    isLoading = false;

                    if (hasMorePhotos()) loadMarkers();
                }, throwable -> {
                    if (!isViewAttached()) return;

                    getView().showMessage(R.string.error);
                    isLoading = false;
                })
        );
    }

    public void refreshMarkers(boolean saveToDatabase) {
        page = 0;
        hasMorePhotos = true;
        loadMarkers(saveToDatabase);
    }

}

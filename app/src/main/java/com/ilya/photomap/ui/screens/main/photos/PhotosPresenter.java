package com.ilya.photomap.ui.screens.main.photos;

import com.ilya.photomap.R;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.data.network.model.PhotoInDTO;
import com.ilya.photomap.repository.PhotoRepository;
import com.ilya.photomap.ui.base.BasePresenter;
import com.ilya.photomap.ui.base.UIState;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Single;

/**
 * Presenter for Main Activity
 */
public class PhotosPresenter<T extends PhotosView> extends BasePresenter<T> {

    private final PhotoRepository repository;
    private int page;
    private boolean isLoading; // are photos loading right now
    private boolean hasMorePhotos;

    public PhotosPresenter() {
        repository = new PhotoRepository();
        page = 0;
        isLoading = false;
        hasMorePhotos = true;
    }

    public boolean hasMorePhotos() {
        return hasMorePhotos;
    }

    public void loadPhotos() {
        loadPhotos(true);
    }

    private void loadPhotos(boolean saveToDatabase) {
        if (isLoading) return;
        isLoading = true;

        if (page == 0) getView().setUIState(UIState.LOADING);

        getCompositeDisposable().add(repository.loadPhotos(page, saveToDatabase)
                .subscribe(photos -> {
                    if (!isViewAttached()) return;

                    getView().setUIState(UIState.CONTENT);
                    getView().displayPhotos(photos, page == 0);

                    if (photos.size() == 0) hasMorePhotos = false;
                    else page++;
                    isLoading = false;
                }, throwable -> {
                    if (!isViewAttached()) return;

                    getView().setUIState(UIState.ERROR);
                    isLoading = false;
                })
        );
    }

    public void refreshPhotos(boolean saveToDatabase) {
        page = 0;
        hasMorePhotos = true;
        loadPhotos(saveToDatabase);
    }

    public void deletePhoto(int idPhoto, int position) {
        getCompositeDisposable().add(repository.deletePhoto(idPhoto)
                .subscribe(responseBody -> {
                    if (!isViewAttached()) return;

                    getView().deletePhoto(position);
                    getView().showMessage(R.string.photo_deleted);
                }, throwable -> {
                    if (!isViewAttached()) return;

                    getView().showMessage(R.string.error);
                })
        );
    }
}

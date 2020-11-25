package com.ilya.photomap.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ilya.photomap.App;
import com.ilya.photomap.data.database.AppDatabase;
import com.ilya.photomap.data.database.dao.PhotoDao;
import com.ilya.photomap.data.database.entities.Comment;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.data.network.api.ImageApi;
import com.ilya.photomap.data.network.model.CommentOutDTO;
import com.ilya.photomap.data.network.model.PhotoInDTO;
import com.ilya.photomap.data.network.model.PhotoOutDTO;
import com.ilya.photomap.ui.base.UIState;
import com.ilya.photomap.util.ListUtil;
import com.ilya.photomap.util.ResponseUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.operators.flowable.FlowablePublish;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Handshake;
import okhttp3.ResponseBody;

public class PhotoRepository {

    /**
     * Database
     */
    private final PhotoDao photoDao;

    /**
     * Network
     */
    private final ImageApi imageApi;

    private final String token;
    private final String login;

    public PhotoRepository() {
        AppDatabase db = AppDatabase.getInstance();
        photoDao = db.photoDao();
        imageApi = App.getServerService().getImageApi();

        token = App.getToken();
        login = App.getLogin();
    }

    public Single<List<Photo>> loadPhotos(int page, boolean saveToDatabase) {
        Single<List<Photo>> request = imageApi.getImages(token, page) // Network request
                .subscribeOn(Schedulers.io())
                // Mapping responseBody to list of items
                .map(responseBody -> {
                    List<PhotoOutDTO> photosOut = ResponseUtil.parseData(responseBody, new TypeToken<List<PhotoOutDTO>>(){}.getType());
                    return ListUtil.map(photosOut, dto -> dto.convertToEntity(login));
                });
        // Inserting new photos to database
        if (saveToDatabase) request = request.doAfterSuccess(photos -> Observable.combineLatest(
                        Observable.just(photos),
                        photoDao.getIds(login).toObservable(), // Getting all existing photos in database (ids)
                        (serverComments, databaseCommentsIds) -> {
                            List<Photo> result = new ArrayList<>();

                            for (Photo photo : serverComments) {
                                if (databaseCommentsIds.contains(photo.id)) continue;
                                result.add(photo);
                            }

                            return result;
                        })
                        .subscribeOn(Schedulers.io())
                        .flatMapCompletable(photoDao::insertAll)
                        .subscribe());

        // Load data from db if error
        return request.onErrorResumeNext(photoDao.getAll(login, page)
                .flatMap(photos -> {
                    if (photos.size() == 0 && page == 0) return Single.error(new RuntimeException("No data is available, please, refresh the page"));
                    return Single.just(photos);
                }))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ResponseBody> addPhoto(PhotoInDTO photo) {
        return imageApi.uploadImage(token, photo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deletePhoto(int id) {
        return imageApi.deleteImage(token, id)
                .subscribeOn(Schedulers.io())
                .andThen(photoDao.delete(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
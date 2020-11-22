package com.ilya.photomap.data.network.api;

import com.ilya.photomap.data.network.model.PhotoInDTO;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Image Api
 */
public interface ImageApi {

    @GET("image")
    Single<ResponseBody> getImages(@Header("Access-Token") String token, @Query("page") int page);

    @POST("image")
    Single<ResponseBody> uploadImage(@Header("Access-Token") String token, @Body PhotoInDTO image);

    @DELETE("image/{id}")
    Single<ResponseBody> deleteImage(@Header("Access-Token") String token, @Path("id") int id);

}

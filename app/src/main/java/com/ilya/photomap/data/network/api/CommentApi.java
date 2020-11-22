package com.ilya.photomap.data.network.api;

import com.ilya.photomap.data.network.model.CommentInDTO;
import com.ilya.photomap.data.network.model.PhotoInDTO;
import com.ilya.photomap.data.network.model.SignUserInDTO;

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
 * Account Api
 */
public interface CommentApi {

    @GET("image/{imageId}/comment")
    Single<ResponseBody> getComments(
            @Header("Access-Token") String token,
            @Path("imageId") int imageId,
            @Query("page") int page);

    @POST("image/{imageId}/comment")
    Single<ResponseBody> leaveComment(
            @Header("Access-Token") String token,
            @Path("imageId") int imageId,
            @Body CommentInDTO comment);

    @DELETE("image/{imageId}/comment/{id}")
    Single<ResponseBody> deleteComment(
            @Header("Access-Token") String token,
            @Path("imageId") int imageId,
            @Path("id") int id);

}

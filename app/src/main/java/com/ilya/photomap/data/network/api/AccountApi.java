package com.ilya.photomap.data.network.api;

import com.ilya.photomap.data.network.model.SignUserInDTO;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Account Api
 */
public interface AccountApi {

    @POST("account/signup")
    Single<ResponseBody> signup(@Body SignUserInDTO signUserIn);

    @POST("account/signin")
    Single<ResponseBody> signin(@Body SignUserInDTO signUserIn);

}

package com.ilya.photomap.data.network;

import android.util.Log;

import com.ilya.photomap.App;
import com.ilya.photomap.data.network.api.AccountApi;
import com.ilya.photomap.data.network.api.CommentApi;
import com.ilya.photomap.data.network.api.ImageApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Server Service
 */
public class ServerService {

    private static final String BASE_URL = "https://junior.balinasoft.com/api/";
    private final ImageApi imageApi;
    private final AccountApi accountApi;
    private final CommentApi commentApi;

    public ServerService() {
        Retrofit retrofit = createRetrofit();

        imageApi = retrofit.create(ImageApi.class);
        accountApi = retrofit.create(AccountApi.class);
        commentApi = retrofit.create(CommentApi.class);
    }

    public ImageApi getImageApi() {
        return imageApi;
    }

    public AccountApi getAccountApi() {
        return accountApi;
    }

    public CommentApi getCommentApi() {
        return commentApi;
    }

    private OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            String token = App.getToken();
            if (token == null) return chain.proceed(chain.request());

            Request request = chain.request().newBuilder()
                    .header("Access-Token", token)
                    .build();

            return chain.proceed(request);
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        return httpClient.build();
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

}

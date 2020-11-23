package com.ilya.photomap.ui.screens.authorization.login;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ilya.photomap.App;
import com.ilya.photomap.R;
import com.ilya.photomap.data.network.api.AccountApi;
import com.ilya.photomap.data.network.model.SignUserInDTO;
import com.ilya.photomap.data.network.model.SignUserOutDTO;
import com.ilya.photomap.ui.base.BasePresenter;
import com.ilya.photomap.ui.base.UIState;
import com.ilya.photomap.util.AppUtil;
import com.ilya.photomap.util.ValidationUtil;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Presenter for Login Fragment
 */
public class LoginPresenter<T extends LoginView> extends BasePresenter<T> {

    /**
     * Api
     */
    private AccountApi accountApi;

    public LoginPresenter() {
        accountApi = App.getServerService().getAccountApi();
    }

    public void login(@NonNull String login, @NonNull String password) {

        // validate email and password
        // validate email and password
        int message = ValidationUtil.checkLoginAndPassword(login, password);
        if (message != ValidationUtil.SUCCESS) {
            getView().showMessage(message);
            return;
        }

        getView().setUIState(UIState.LOADING);

        SignUserInDTO signUserIn = new SignUserInDTO();
        signUserIn.login = login;
        signUserIn.password = password;

        getCompositeDisposable().add(accountApi.signin(signUserIn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    if (!isViewAttached()) return;

                    try {
                        Gson gson = new Gson();
                        JsonObject body = gson.fromJson(responseBody.string(), JsonObject.class);
                        SignUserOutDTO signUserOut = gson.fromJson(body.get("data"), SignUserOutDTO.class);

                        App.saveUserInfo(signUserOut.token, signUserOut.login);

                        getView().loginComplete();
                    } catch (IOException e) {
                        getView().setUIState(UIState.CONTENT);
                        getView().showMessage(R.string.error);
                    }
                }, throwable -> {
                    if (!isViewAttached()) return;

                    getView().setUIState(UIState.CONTENT);
                    getView().showMessage(R.string.error);
                })
        );
    }
}

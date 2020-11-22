package com.ilya.photomap.ui.screens.authorization.signup;

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

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Presenter for Signup Fragment
 */
public class SignupPresenter<T extends SignupView> extends BasePresenter<T> {

    /**
     * Api
     */
    private AccountApi accountApi;

    public SignupPresenter() {
        accountApi = App.getServerService().getAccountApi();
    }

    public void signup(@NonNull String login, @NonNull String password) {

        // validate email and password
        if (TextUtils.isEmpty(login)) {
            getView().showMessage(R.string.empty_login);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            getView().showMessage(R.string.empty_password);
            return;
        }

        getView().setUIState(UIState.LOADING);

        SignUserInDTO signUserIn = new SignUserInDTO();
        signUserIn.login = login;
        signUserIn.password = password;

        getCompositeDisposable().add(accountApi.signup(signUserIn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    if (!isViewAttached()) return;

                    try {
                        Gson gson = new Gson();
                        JsonObject body = gson.fromJson(responseBody.string(), JsonObject.class);
                        SignUserOutDTO signUserOut = gson.fromJson(body.get("data"), SignUserOutDTO.class);

                        App.saveUserInfo(signUserOut.token, signUserOut.login);

                        getView().signupComplete();
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

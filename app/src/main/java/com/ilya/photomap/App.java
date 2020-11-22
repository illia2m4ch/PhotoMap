package com.ilya.photomap;

import android.app.Application;
import android.content.SharedPreferences;

import com.ilya.photomap.data.network.ServerService;

import static com.ilya.photomap.util.Constants.LOGIN_KEY;
import static com.ilya.photomap.util.Constants.TOKEN_KEY;

public class App extends Application {

    /**
     * Singleton
     */
    private static App instance;

    private ServerService serverService;
    private SharedPreferences settings;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        settings = getSharedPreferences("app", MODE_PRIVATE);
    }

    public static App getInstance() {
        return instance;
    }

    public static ServerService getServerService() {
        if (instance.serverService == null) instance.serverService = new ServerService();
        return instance.serverService;
    }

    private static SharedPreferences getSettings() {
        return instance.settings;
    }

    public static String getToken() {
        return getSettings().getString(TOKEN_KEY, null);
    }

    public static String getLogin() {
        return getSettings().getString(LOGIN_KEY, null);
    }

    public static void saveUserInfo(String token, String login) {
        getSettings().edit()
                .putString(TOKEN_KEY, token)
                .putString(LOGIN_KEY, login)
                .apply();
    }

    public static void logout() {
        saveUserInfo(null, null);
    }
}

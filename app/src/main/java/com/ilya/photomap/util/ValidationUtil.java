package com.ilya.photomap.util;

import android.text.TextUtils;

import com.ilya.photomap.R;

public class ValidationUtil {

    public static final int SUCCESS = 0;

    /**
     * Returns string id if error or 0 if login is valid
     */
    public static int checkLogin(String login) {
        if (TextUtils.isEmpty(login)) return R.string.empty_login;
        if (login.length() < 4) return R.string.login_min_length;
        if (login.length() > 32) return R.string.login_max_length;
        if (!login.matches("[a-z0-9_\\-.@]+")) return R.string.invalid_login;
        return SUCCESS;
    }

    /**
     * Returns string id if error or 0 if password is valid
     */
    public static int checkPassword(String password) {
        if (TextUtils.isEmpty(password)) return R.string.empty_password;
        if (password.length() < 8) return R.string.password_min_length;
        if (password.length() > 500) return R.string.password_max_length;
        return SUCCESS;
    }

    public static int checkLoginAndPassword(String login, String password) {
        int loginMessage = checkLogin(login);
        if (loginMessage != SUCCESS) return loginMessage;
        return checkPassword(password);
    }

}

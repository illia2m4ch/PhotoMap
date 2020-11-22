package com.ilya.photomap.ui.screens.authorization.login;

import com.ilya.photomap.ui.base.BaseView;
import com.ilya.photomap.ui.base.InfoView;

/**
 * View for Login Fragment
 */
public interface LoginView extends BaseView, InfoView {

    void openSignup();
    void loginComplete();

}

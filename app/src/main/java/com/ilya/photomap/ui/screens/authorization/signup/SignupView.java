package com.ilya.photomap.ui.screens.authorization.signup;

import com.ilya.photomap.ui.base.BaseView;
import com.ilya.photomap.ui.base.InfoView;

/**
 * View for Signup Fragment
 */
public interface SignupView extends BaseView, InfoView {

    void openLogin();
    void signupComplete();

}

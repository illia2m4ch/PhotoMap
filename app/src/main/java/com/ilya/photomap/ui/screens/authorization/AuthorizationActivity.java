package com.ilya.photomap.ui.screens.authorization;

import android.os.Bundle;

import com.ilya.photomap.R;
import com.ilya.photomap.ui.screens.authorization.login.LoginFragment;
import com.ilya.photomap.ui.screens.authorization.signup.SignupFragment;
import com.ilya.photomap.ui.base.BaseActivity;

/**
 * Authorization Activity
 */
public class AuthorizationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        init();
    }

    private void init() {
        showLogin();
    }

    public void showLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.authorization_fragment, new LoginFragment())
                .commit();
    }

    public void showSignup() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.authorization_fragment, new SignupFragment())
                .commit();
    }
}
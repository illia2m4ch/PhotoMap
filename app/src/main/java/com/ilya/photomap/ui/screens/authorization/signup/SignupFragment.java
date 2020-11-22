package com.ilya.photomap.ui.screens.authorization.signup;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.ilya.photomap.R;
import com.ilya.photomap.ui.base.UIState;
import com.ilya.photomap.ui.screens.authorization.AuthorizationActivity;
import com.ilya.photomap.ui.base.BaseFragment;
import com.ilya.photomap.ui.screens.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Login Fragment
 */
public class SignupFragment extends BaseFragment implements SignupView {

    /**
     * Binding
     */
    Unbinder unbinder;

    /**
     * Presenter
     */
    SignupPresenter<SignupView> presenter;

    /**
     * Views
     */
    @BindView(R.id.login)
    TextInputLayout login;
    @BindView(R.id.password)
    TextInputLayout password;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Binding
        unbinder = ButterKnife.bind(this, view);

        // Register UI states
        registerUIState(UIState.LOADING, view.findViewById(R.id.state_loading));
        registerUIState(UIState.CONTENT, view.findViewById(R.id.state_content));
        setUIState(UIState.CONTENT);

        // Presenter
        presenter = new SignupPresenter<>();
        presenter.attachView(this);

        return view;
    }

    @OnClick(R.id.btnSignup)
    void onClickSignup() {
        String login = this.login.getEditText().getText().toString();
        String password = this.password.getEditText().getText().toString();
        presenter.signup(login, password);
    }

    @Override
    @OnClick(R.id.btnLogin)
    public void openLogin() {
        AuthorizationActivity activity = (AuthorizationActivity) getActivity();
        activity.showLogin();
    }

    @Override
    public void signupComplete() {
        open(MainActivity.class, true);
    }

    @Override
    public void showMessage(int resId) {
        showSnackbar(resId);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        presenter.detachView();
        super.onDestroyView();
    }
}
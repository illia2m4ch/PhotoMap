package com.ilya.photomap.ui.screens.authorization.login;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
public class LoginFragment extends BaseFragment implements LoginView {

    /**
     * Binding
     */
    Unbinder unbinder;

    /**
     * Presenter
     */
    LoginPresenter<LoginView> presenter;

    /**
     * Views
     */
    @BindView(R.id.login)
    TextInputLayout login;
    @BindView(R.id.password)
    TextInputLayout password;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Binding
        unbinder = ButterKnife.bind(this, view);

        // Register UI states
        registerUIState(UIState.LOADING, view.findViewById(R.id.state_loading));
        registerUIState(UIState.CONTENT, view.findViewById(R.id.state_content));
        setUIState(UIState.CONTENT);

        // Presenter
        presenter = new LoginPresenter<>();
        presenter.attachView(this);

        return view;
    }

    @OnClick(R.id.btnLogin)
    void onClickLogin() {
        String login = this.login.getEditText().getText().toString();
        String password = this.password.getEditText().getText().toString();
        presenter.login(login, password);
    }

    @Override
    @OnClick(R.id.btnSignup)
    public void openSignup() {
        AuthorizationActivity activity = (AuthorizationActivity) getActivity();
        activity.showSignup();
    }

    @Override
    public void loginComplete() {
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
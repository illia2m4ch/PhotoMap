package com.ilya.photomap.ui.base;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class BaseFragment extends Fragment implements BaseView {

    /**
     * Map of possible UI states and views related to these states
     */
    private SparseArray<View> UIStates = new SparseArray<>();
    private UIState currentState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!(getActivity() instanceof BaseActivity)) {
            throw new IllegalStateException("The activity of BaseFragment should extends the BaseActivity");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void setUIState(UIState state) {
        if (currentState == state) return;
        if (currentState != null) {
            UIStates.get(currentState.getValue()).setVisibility(View.GONE);
        }
        View view = UIStates.get(state.getValue());
        if (view == null) throw new IllegalStateException("UIState " + state.name() +
                " is not found. Please, use registerUIState() method in onCreateView()");
        view.setVisibility(View.VISIBLE);
        currentState = state;
    }

    @Override
    public void registerUIState(UIState state, View view) {
        UIStates.put(state.getValue(), view);
        view.setVisibility(View.GONE);
    }

    public void showToast(int resId) {
        getBaseActivity().showToast(resId);
    }

    public void showSnackbar(View view, int resId) {
        getBaseActivity().showSnackbar(view, resId);
    }

    public void showSnackbar(int resId) {
        getBaseActivity().showSnackbar(getView(), resId);
    }

    public void open(Class<?> activityClass, Bundle bundle, boolean closeCurrentActivity) {
        getBaseActivity().open(activityClass, bundle, closeCurrentActivity);
    }

    public void open(Class<?> activityClass, boolean closeCurrentActivity) {
        getBaseActivity().open(activityClass, closeCurrentActivity);
    }

    public void requestPermission(String permission, int code) {
        getBaseActivity().requestPermission(permission, code);
    }

    protected void showLoadingDialog(int resId) {
        getBaseActivity().showLoadingDialog(resId);
    }

    protected void hideLoadingDialog() {
        getBaseActivity().hideLoadingDialog();
    }
}

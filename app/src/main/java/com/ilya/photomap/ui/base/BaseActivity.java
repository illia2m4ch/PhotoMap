package com.ilya.photomap.ui.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity implements BaseView {

    private SharedPreferences settings;

    private ProgressDialog loadingDialog;

    /**
     * Map of possible UI states and views related to these states
     */
    private SparseArray<View> UIStates = new SparseArray<>();
    private UIState currentState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
    }

    @Override
    public void setUIState(UIState state) {
        if (currentState == state) return;
        if (currentState != null) {
            UIStates.get(currentState.getValue()).setVisibility(View.GONE);
        }
        View view = UIStates.get(state.getValue());
        if (view == null) throw new IllegalStateException("UIState " + state.name() +
                " is not found. Please, use registerUIState() method in onCreate()");
        view.setVisibility(View.VISIBLE);
        currentState = state;
    }

    @Override
    public void registerUIState(UIState state, View view) {
        UIStates.put(state.getValue(), view);
        view.setVisibility(View.GONE);
    }

    public void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void showSnackbar(View view, int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();
    }

    public void showSnackbar(View view, int resId, View anchor) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).setAnchorView(anchor).show();
    }

    public void open(Class<?> activityClass, Bundle bundle, boolean closeCurrentActivity) {
        Intent intent = new Intent(this, activityClass);
        if (bundle != null) intent.putExtras(bundle);
        startActivity(intent);
        if (closeCurrentActivity) finish();
    }

    public void open(Class<?> activityClass, boolean closeCurrentActivity) {
        open(activityClass, null, closeCurrentActivity);
    }

    protected void hideStatusBar() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected SharedPreferences getSettings() {
        if (settings == null) {
            settings = getSharedPreferences(BaseActivity.class.getName(), MODE_PRIVATE);
        }
        return settings;
    }

    protected void requestPermission(String permission, int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // Add alert dialog
            ActivityCompat.requestPermissions(this, new String[] { permission }, code);
        } else {
            ActivityCompat.requestPermissions(this, new String[] { permission }, code);
        }
    }

    protected void showLoadingDialog(int resId) {
        loadingDialog.setMessage(getString(resId));
        loadingDialog.show();
    }

    protected void hideLoadingDialog() {
        loadingDialog.dismiss();
    }
}

package com.ilya.photomap.ui.screens.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.ilya.photomap.App;
import com.ilya.photomap.R;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.ui.screens.authorization.AuthorizationActivity;
import com.ilya.photomap.ui.base.BaseActivity;
import com.ilya.photomap.ui.screens.main.map.MapFragment;
import com.ilya.photomap.ui.screens.main.photos.PhotosFragment;
import com.ilya.photomap.ui.services.LocationFinder;
import com.ilya.photomap.util.AppUtil;

import java.util.List;

import io.reactivex.Single;

/**
 * Main activity
 */
public class MainActivity extends BaseActivity implements MainView {

    /**
     * Request codes
     */
    final int REQUEST_PERMISSIONS_CODE = 0;
    final int REQUEST_PHOTO = 1;

    /**
     * Presenter
     */
    MainPresenter<MainView> presenter;

    /**
     * Views
     */
    DrawerLayout drawer;
    CoordinatorLayout container;
    NavigationView navigation;

    /**
     * Fragments
     */
    public static final String TAG_PHOTOS = "photos";
    public static final String TAG_MAP = "map";
    Fragment currentFragment;
    int currentFragmentId = -1;

    /**
     * Location
     */
    LocationFinder locationFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Views
        container = findViewById(R.id.container);
        // FAB
        findViewById(R.id.btn_add_photo).setOnClickListener(v -> {
            presenter.tryOpenCamera(this, locationFinder.isGpsEnabled());
        });
        initNavigation();

        // Location
        locationFinder = new LocationFinder(this);

        // Presenter
        presenter = new MainPresenter<>();
        presenter.attachView(this);
        presenter.checkCurrentToken();
    }

    /**
     * Navigation
     */
    private void initNavigation() {
        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawer = findViewById(R.id.drawer);

        // Navigation
        navigation = findViewById(R.id.navigation);
        View navHeader = navigation.getHeaderView(0);
        TextView login = navHeader.findViewById(R.id.login);
        login.setText(App.getLogin());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        // If fragments have already added
        if (fragmentManager.findFragmentByTag(TAG_PHOTOS) != null) return;

        PhotosFragment photosFragment = new PhotosFragment();
        photosFragment.setRetainInstance(true);

        MapFragment mapFragment = new MapFragment();
        mapFragment.setRetainInstance(true);

        // Adding all fragments
        fragmentManager.beginTransaction()
                .add(R.id.fragment, photosFragment, TAG_PHOTOS).hide(photosFragment)
                .add(R.id.fragment, mapFragment, TAG_MAP).hide(mapFragment)
                .runOnCommit(() -> displayFragment(R.id.photos))
                .commit();

        navigation.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            displayFragment(id);
            return true;
        });
    }

        @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displayFragment(int itemId) {
        Fragment fragment = null;
        int titleId = 0;

        if (itemId == R.id.logout) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_logout)
                    .setPositiveButton(R.string.yes, (dialog, which) -> presenter.logout())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            return;
        }

        if (itemId == R.id.photos) {
            fragment = getSupportFragmentManager().findFragmentByTag(TAG_PHOTOS);
            titleId = R.string.photos;
        }
        else if (itemId == R.id.map) {
            fragment = getSupportFragmentManager().findFragmentByTag(TAG_MAP);
            titleId = R.string.map;
        }

        if (fragment != null) {
            getSupportActionBar().setTitle(titleId);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (currentFragment != null) ft.hide(currentFragment);
            ft.show(fragment);
            ft.commit();

            if (currentFragmentId != -1) navigation.getMenu().findItem(currentFragmentId).setChecked(false);
            navigation.getMenu().findItem(itemId).setChecked(true);

            currentFragment = fragment;
            currentFragmentId = itemId;
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void openAuthorization() {
        open(AuthorizationActivity.class, true);
    }

    @Override
    public void showMessage(int resId) {
        showSnackbar(container, resId);
    }

    @Override
    public void openCamera() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePhotoIntent, REQUEST_PHOTO);
    }

    /**
     * Parameter saveToDatabase allows to avoid duplicate requests
     * As a result, the error of saving two items with the same id to the database has been fixed
     * That's not so brilliant solution maybe in next iterations of refactoring it will be optimized :)
     */
    @Override
    public void notifyPhotosUpdated() {
        ((PhotosFragment) getSupportFragmentManager().findFragmentByTag(TAG_PHOTOS))
                .refreshPhotos(true);
        ((MapFragment) getSupportFragmentManager().findFragmentByTag(TAG_MAP))
                .refreshMarkers(false);
    }

    @Override
    public void deletePhoto(int id, int position) {
        presenter.deletePhoto(id, position);
    }

    @Override
    public void notifyPhotoDeleted(int id, int position) {
        ((PhotosFragment) getSupportFragmentManager().findFragmentByTag(TAG_PHOTOS))
                .deletePhoto(position);
        ((MapFragment) getSupportFragmentManager().findFragmentByTag(TAG_MAP))
                .removeMarker(id);
    }

    @Override
    public void requestPermission(String permission) {
        super.requestPermission(permission, REQUEST_PERMISSIONS_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {

            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) return;
            }
            presenter.tryOpenCamera(this, locationFinder.isGpsEnabled());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            String base64Photo = AppUtil.convertBitmapToBase64(photo);

            showLoadingDialog(R.string.please_wait);
            locationFinder.requestLocation(location -> {
                if (location == null) showMessage(R.string.empty_location);
                else {
                    presenter.addPhoto(base64Photo, location.getLatitude(), location.getLongitude());
                }

                hideLoadingDialog();
            });
        }
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }
}
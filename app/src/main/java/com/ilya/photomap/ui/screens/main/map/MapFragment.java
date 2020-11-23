package com.ilya.photomap.ui.screens.main.map;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ilya.photomap.R;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.ui.base.BaseFragment;
import com.ilya.photomap.ui.base.UIState;
import com.ilya.photomap.ui.screens.main.photos.PhotosPresenter;
import com.ilya.photomap.ui.screens.main.photos.PhotosView;
import com.ilya.photomap.ui.screens.photo.PhotoActivity;
import com.ilya.photomap.util.AppUtil;
import com.ilya.photomap.util.Constants;
import com.ilya.photomap.util.DateUtil;
import com.ilya.photomap.util.ListUtil;

import java.util.List;

import io.reactivex.Single;

public class MapFragment extends BaseFragment implements MapView, OnMapReadyCallback {

    /**
     * Presenter
     */
    MapPresenter<MapView> presenter;

    /**
     * Google map
     */
    GoogleMap map;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Register UI states
        registerUIState(UIState.LOADING, view.findViewById(R.id.state_loading));
        registerUIState(UIState.CONTENT, view.findViewById(R.id.map));

        // A short pause for the fragment to be created
        setUIState(UIState.LOADING);
        new Handler().postDelayed(() -> {
            SupportMapFragment mapFragment = new SupportMapFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map, mapFragment)
                    .commit();

            mapFragment.getMapAsync(this);

            setUIState(UIState.CONTENT);
        }, 300);

        // Presenter
        presenter = new MapPresenter<>();
        presenter.attachView(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnInfoWindowClickListener(marker -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.PHOTO_KEY, (Photo) marker.getTag());
            open(PhotoActivity.class, bundle, false);
        });

        presenter.loadMarkers();
    }

    @Override
    public void displayMarkers(List<Photo> photos) {

        ListUtil.map(photos, photo -> {
            LatLng coordinates = new LatLng(photo.latitude, photo.longitude);
            MarkerOptions marker = new MarkerOptions()
                    .position(coordinates)
                    .title(DateUtil.getFormattedDate(photo.date))
                    .snippet(getString(R.string.click_to_open));

            AppUtil.uploadPhotoAsync(photo.url, getContext(), new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    marker.icon(BitmapDescriptorFactory.fromBitmap(resource));
                    Marker result = map.addMarker(marker);
                    result.setTag(photo);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) { }
            });

            return marker;
        });
    }

    @Override
    public void refreshMarkers(boolean saveToDatabase) {
        presenter.refreshMarkers(saveToDatabase);
    }

    @Override
    public void showMessage(int resId) {
        if (isVisible()) showSnackbar(resId);
    }
}
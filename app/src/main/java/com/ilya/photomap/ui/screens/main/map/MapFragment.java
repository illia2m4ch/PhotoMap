package com.ilya.photomap.ui.screens.main.map;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ilya.photomap.R;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.ui.base.BaseFragment;
import com.ilya.photomap.ui.base.UIState;
import com.ilya.photomap.ui.screens.main.photos.PhotosPresenter;
import com.ilya.photomap.ui.screens.main.photos.PhotosView;

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
        presenter.loadMarkers();
    }

    @Override
    public void displayMarkers(List<MarkerOptions> markers) {
        LatLng latLng = markers.get(0).getPosition();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f));

        for (MarkerOptions marker : markers) map.addMarker(marker);
    }

    @Override
    public void refreshMarkers(boolean saveToDatabase) {
        presenter.refreshMarkers(saveToDatabase);
    }

    @Override
    public void showMessage(int resId) {
        showSnackbar(resId);
    }
}
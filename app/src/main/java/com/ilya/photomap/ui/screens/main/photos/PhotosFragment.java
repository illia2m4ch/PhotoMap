package com.ilya.photomap.ui.screens.main.photos;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ilya.photomap.R;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.ui.adapters.PhotosAdapter;
import com.ilya.photomap.ui.base.BaseFragment;
import com.ilya.photomap.ui.base.UIState;
import com.ilya.photomap.ui.screens.photo.PhotoActivity;
import com.ilya.photomap.util.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;

public class PhotosFragment extends BaseFragment implements PhotosView {

    /**
     * Binding
     */
    Unbinder unbinder;

    /**
     * Presenter
     */
    PhotosPresenter<PhotosView> presenter;

    /**
     * Views
     */
    @BindView(R.id.photos)
    RecyclerView photos;
    PhotosAdapter photosAdapter = new PhotosAdapter();

    public PhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        // Binding
        unbinder = ButterKnife.bind(this, view);

        // Views
        photosAdapter.setOnDeleteItemListener((photo, position) -> {
            presenter.deletePhoto(photo.id, position);
        });
        photosAdapter.setOnClickItemListener(photo -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.PHOTO_KEY, photo);
            open(PhotoActivity.class, bundle, false);
        });
        photosAdapter.setOnLoadMoreListener(() -> {
            if (presenter.hasMorePhotos()) presenter.loadPhotos();
        });
        photos.setAdapter(photosAdapter);

        // Register UI states
        registerUIState(UIState.EMPTY, view.findViewById(R.id.state_empty));
        registerUIState(UIState.LOADING, view.findViewById(R.id.state_loading));
        registerUIState(UIState.CONTENT, view.findViewById(R.id.photos));
        LinearLayout stateError = view.findViewById(R.id.state_error);
        stateError.findViewById(R.id.btn_reload).setOnClickListener(v -> presenter.loadPhotos());
        registerUIState(UIState.ERROR, stateError);

        // Presenter
        presenter = new PhotosPresenter<>();
        presenter.attachView(this);
        presenter.loadPhotos();

        return view;
    }

    @Override
    public void displayPhotos(List<Photo> photos, boolean isFirstPage) {
        if ((photos == null || photos.size() == 0) && isFirstPage) {
            setUIState(UIState.EMPTY);
            return;
        }
        else setUIState(UIState.CONTENT);

        if (isFirstPage) photosAdapter.loadPhotos(photos);
        else photosAdapter.addPhotos(photos);
    }

    @Override
    public void refreshPhotos(boolean saveToDatabase) {
        presenter.refreshPhotos(saveToDatabase);
    }

    @Override
    public void deletePhoto(int position) {
        photosAdapter.deletePhoto(position);
        if (photosAdapter.getItemCount() == 0) setUIState(UIState.EMPTY);
    }

    @Override
    public void showMessage(int resId) {
        if (isVisible()) showSnackbar(resId);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        presenter.detachView();
        super.onDestroyView();
    }
}
package com.ilya.photomap.ui.screens.photo;

import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.photomap.R;
import com.ilya.photomap.data.database.entities.Comment;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.ui.adapters.CommentsAdapter;
import com.ilya.photomap.ui.base.BaseActivity;
import com.ilya.photomap.ui.base.UIState;
import com.ilya.photomap.ui.screens.main.photos.PhotosPresenter;
import com.ilya.photomap.ui.screens.main.photos.PhotosView;
import com.ilya.photomap.util.AppUtil;
import com.ilya.photomap.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoActivity extends BaseActivity implements PhotoView {

    /**
     * Presenter
     */
    PhotoPresenter<PhotoView> presenter;

    /**
     * Views
     */
    @BindView(R.id.container)
    CoordinatorLayout container;
    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.comments)
    RecyclerView comments;
    CommentsAdapter commentsAdapter = new CommentsAdapter();
    @BindView(R.id.text)
    EditText text;
    @BindView(R.id.btn_send)
    ImageView btnSend;
    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout; // for snackbar as an anchor view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // Binding
        ButterKnife.bind(this);

        // Views
        btnSend.setOnClickListener(v -> {
            presenter.addComment(text.getText().toString());

            // Hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(text.getApplicationWindowToken(),0);
        });

        commentsAdapter.setOnDeleteItemListener((comment, position) -> {
            presenter.deleteComment(comment.id, position);
        });
        commentsAdapter.setOnLoadMoreListener(() -> {
            if (presenter.hasMoreComments()) presenter.loadComments();
        });
        comments.setAdapter(commentsAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            showMessage(R.string.error);
            return;
        }

        Photo photo = bundle.getParcelable(Constants.PHOTO_KEY);
        displayPhoto(photo);

        // Register UI states
        registerUIState(UIState.EMPTY, findViewById(R.id.state_empty));
        registerUIState(UIState.LOADING, findViewById(R.id.state_loading));
        registerUIState(UIState.CONTENT, findViewById(R.id.comments));
        LinearLayout stateError = findViewById(R.id.state_error);
        stateError.findViewById(R.id.btn_reload).setOnClickListener(v -> presenter.loadComments());
        registerUIState(UIState.ERROR, stateError);

        // Presenter
        presenter = new PhotoPresenter<>(photo.id);
        presenter.attachView(this);
        presenter.loadComments();
    }

    public void displayPhoto(Photo photo) {
        // Displaying photo
        AppUtil.uploadPhotoAsync(photo.url, this.photo);
    }

    @Override
    public void displayComments(List<Comment> photos, boolean isFirstPage) {
        if (photos.size() == 0 && isFirstPage) {
            setUIState(UIState.EMPTY);
            return;
        }
        else setUIState(UIState.CONTENT);

        if (isFirstPage) commentsAdapter.loadComments(photos);
        else commentsAdapter.addComments(photos);
    }

    @Override
    public void deleteComment(int position) {
        commentsAdapter.deletePhoto(position);
        if (commentsAdapter.getItemCount() == 0) setUIState(UIState.EMPTY);
    }

    @Override
    public void showMessage(int resId) {
        showSnackbar(container, resId, bottomLayout);
        if (resId != R.string.error) text.setText("");
    }
}
package com.ilya.photomap.ui.screens.photo;

import android.text.TextUtils;
import android.util.Log;

import com.ilya.photomap.R;
import com.ilya.photomap.data.network.model.CommentInDTO;
import com.ilya.photomap.repository.CommentRepository;
import com.ilya.photomap.ui.base.BasePresenter;
import com.ilya.photomap.ui.base.UIState;

/**
 * Presenter for Photo Activity
 */
public class PhotoPresenter<T extends PhotoView> extends BasePresenter<T> {

    private final CommentRepository repository;
    private int page;
    private boolean isLoading; // are photos loading right now
    private boolean hasMoreComments;

    public PhotoPresenter(int idPhoto) {
        repository = new CommentRepository(idPhoto);
        page = 0;
        isLoading = false;
        hasMoreComments = true;
    }

    public boolean hasMoreComments() {
        return hasMoreComments;
    }

    public void loadComments() {
        Log.d("MY_TAG", "load comments");
        if (isLoading) return;
        isLoading = true;

        if (page == 0) getView().setUIState(UIState.LOADING);

        getCompositeDisposable().add(repository.loadComments(page)
                .subscribe(comments -> {
                    if (!isViewAttached()) return;

                    getView().setUIState(UIState.CONTENT);
                    getView().displayComments(comments, page == 0);

                    if (comments.size() == 0) hasMoreComments = false;
                    else page++;
                    isLoading = false;
                }, throwable -> {
                    if (!isViewAttached()) return;

                    getView().setUIState(UIState.ERROR);
                    isLoading = false;
                })
        );
    }

    public void refreshComments() {
        page = 0;
        hasMoreComments = true;
        loadComments();
    }

    public void addComment(String text) {
        String trimmedText = text.trim();

        if (TextUtils.isEmpty(trimmedText)) {
            getView().showMessage(R.string.empty_text);
            return;
        }

        CommentInDTO commentIn = new CommentInDTO();
        commentIn.text = trimmedText;

        getCompositeDisposable().add(repository.addComment(commentIn)
                .subscribe(responseBody -> {
                    if (!isViewAttached()) return;

                    getView().showMessage(R.string.comment_added);
                    refreshComments();
                }, throwable -> {
                    if (!isViewAttached()) return;
                    getView().showMessage(R.string.error);
                })
        );
    }

    public void deleteComment(int idPhoto, int position) {
        getCompositeDisposable().add(repository.deleteComment(idPhoto)
                .subscribe(responseBody -> {
                    if (!isViewAttached()) return;

                    getView().deleteComment(position);
                    getView().showMessage(R.string.comment_deleted);
                }, throwable -> {
                    if (!isViewAttached()) return;

                    getView().showMessage(R.string.error);
                })
        );
    }
}

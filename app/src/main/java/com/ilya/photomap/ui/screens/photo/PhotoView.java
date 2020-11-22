package com.ilya.photomap.ui.screens.photo;

import com.ilya.photomap.data.database.entities.Comment;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.ui.base.BaseView;
import com.ilya.photomap.ui.base.InfoView;

import java.util.List;

public interface PhotoView extends BaseView, InfoView {

    void displayComments(List<Comment> photos, boolean isFirstPage);
    void deleteComment(int position);

}

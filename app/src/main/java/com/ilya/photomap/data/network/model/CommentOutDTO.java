package com.ilya.photomap.data.network.model;

import com.ilya.photomap.data.database.entities.Comment;
import com.ilya.photomap.data.database.entities.Photo;

/**
 * Representation of api model CommentDtoOut
 */
public class CommentOutDTO {

    public int id;
    public long date;
    public String text;

    public Comment convertToEntity(int imageId) {
        Comment comment = new Comment();
        comment.id = id;
        comment.date = date;
        comment.text = text;
        comment.photoId = imageId;

        return comment;
    }

}

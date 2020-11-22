package com.ilya.photomap.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.ilya.photomap.data.database.entities.Comment;
import com.ilya.photomap.data.database.entities.Photo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface CommentDao {

    @Query("SELECT * FROM comment where photoId = :photoId order by date desc limit :page * 20, 20")
    Single<List<Comment>> getAll(int photoId, int page);

    @Query("SELECT id FROM comment")
    Single<List<Integer>> getIds();

    @Insert
    Completable insertAll(List<Comment> comments);

    @Query("delete from comment")
    Completable deleteAll();

    @Query("delete from comment where id = :id")
    Completable delete(int id);

}

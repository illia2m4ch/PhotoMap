package com.ilya.photomap.data.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ilya.photomap.data.database.entities.Photo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface PhotoDao {

    @Query("SELECT * FROM photo where userLogin = :userLogin order by date desc limit :page * 20, 20")
    Single<List<Photo>> getAll(String userLogin, int page);

    @Query("SELECT id FROM photo where userLogin = :userLogin")
    Single<List<Integer>> getIds(String userLogin);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertAll(List<Photo> photos);

    @Query("delete from photo")
    Completable deleteAll();

    @Query("delete from photo where id = :id")
    Completable delete(int id);

}

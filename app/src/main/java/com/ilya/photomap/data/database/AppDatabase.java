package com.ilya.photomap.data.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ilya.photomap.App;
import com.ilya.photomap.data.database.dao.CommentDao;
import com.ilya.photomap.data.database.dao.PhotoDao;
import com.ilya.photomap.data.database.entities.Comment;
import com.ilya.photomap.data.database.entities.Photo;

@Database(entities = {Photo.class, Comment.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance() {
        if (instance == null) {
            instance = Room.databaseBuilder(App.getInstance(), AppDatabase.class, "appdatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract PhotoDao photoDao();
    public abstract CommentDao commentDao();
}
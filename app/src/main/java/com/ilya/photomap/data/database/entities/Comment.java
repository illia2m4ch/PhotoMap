package com.ilya.photomap.data.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "comment", foreignKeys = @ForeignKey(entity = Photo.class, parentColumns = "id", childColumns = "photoId"))
public class Comment {

    @PrimaryKey
    public int id;
    public long date;
    public String text;
    public int photoId;

}

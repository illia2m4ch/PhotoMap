package com.ilya.photomap.data.database.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "photo")
public class Photo implements Parcelable {

    @PrimaryKey
    public int id;
    public String url;
    public long date;
    public double latitude;
    public double longitude;

    public Photo() {
        // Required empty constructor
    }

    protected Photo(Parcel in) {
        id = in.readInt();
        url = in.readString();
        date = in.readLong();
        latitude = in.readLong();
        longitude = in.readLong();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeLong(date);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}

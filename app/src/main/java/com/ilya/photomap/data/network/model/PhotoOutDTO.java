package com.ilya.photomap.data.network.model;

import com.ilya.photomap.data.database.entities.Photo;

/**
 * Representation of api model ImageDtoOut
 */
public class PhotoOutDTO {

    public int id;
    public String url;
    public long date;
    public double lat;
    public double lng;

    public Photo convertToEntity(String userLogin) {
        Photo photo = new Photo();
        photo.id = id;
        photo.url = url;
        photo.date = date;
        photo.latitude = lat;
        photo.longitude = lng;
        photo.userLogin = userLogin;

        return photo;
    }

}

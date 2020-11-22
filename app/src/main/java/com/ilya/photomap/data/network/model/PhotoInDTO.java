package com.ilya.photomap.data.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Representation of api model ImageDtoIn
 */
public class PhotoInDTO {

    @SerializedName("base64Image")
    public String base64Photo;
    public long date;
    public double lat;
    public double lng;

}

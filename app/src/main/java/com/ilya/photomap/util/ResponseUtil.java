package com.ilya.photomap.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;

public class ResponseUtil {

    // type example: new TypeToken<List<Photo>>(){}.getType();
    public static <T>T parseData(ResponseBody responseBody, Type type) {
        try {
            Gson gson = new Gson();
            JsonObject body = gson.fromJson(responseBody.string(), JsonObject.class);
            return gson.fromJson(body.get("data"), type);
        } catch (IOException e) {
            throw new RuntimeException("Error while parsing response");
        }
    }

}

/*
try {
            Gson gson = new Gson();
            JsonObject body = gson.fromJson(responseBody.string(), JsonObject.class);
            Type type = new TypeToken<T>(){}.getType();
            return gson.fromJson(body.get("data"), type);
        } catch (IOException e) {
            return null;
        }
 */
package com.ilya.photomap.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.ilya.photomap.R;

import java.io.ByteArrayOutputStream;

public class AppUtil {

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static String convertBitmapToBase64(Bitmap photo) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static void uploadPhotoAsync(String url, ImageView into) {
        Glide.with(into)
                .load(url)
                .centerCrop()
                .placeholder(R.color.gray_cc)
                .into(into);
    }

    public static void uploadPhotoAsync(String url, Context context, CustomTarget<Bitmap> target) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions.overrideOf(128, 128))
                .placeholder(R.color.gray_cc)
                .into(target);
    }
}

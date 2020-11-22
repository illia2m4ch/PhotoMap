package com.ilya.photomap.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ilya.photomap.R;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.util.AppUtil;
import com.ilya.photomap.util.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    /**
     * On Delete Item Interface
     */
    public interface OnDeleteItemListener {
        void onDeleteItem(Photo photo, int position);
    }

    private OnDeleteItemListener onDeleteItemListener;

    /**
     * On Click Item Interface
     */
    public interface OnClickItemListener {
        void onClickItem(Photo photo);
    }

    private OnClickItemListener onClickItemListener;

    /**
     * On Load More Interface
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private OnLoadMoreListener onLoadMoreListener;

    private List<Photo> photos;

    public PhotosAdapter() {
        photos = new ArrayList<>();
    }

    public PhotosAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    public void loadPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public void addPhotos(List<Photo> photos) {
        int itemCount = getItemCount();
        this.photos.addAll(photos);
        notifyItemRangeInserted(itemCount, photos.size());
    }

    public void setOnDeleteItemListener(OnDeleteItemListener onDeleteItemListener) {
        this.onDeleteItemListener = onDeleteItemListener;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void deletePhoto(int position) {
        photos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Photo photo = photos.get(position);
        Context context = h.root.getContext();

        AppUtil.uploadPhotoAsync(photo.url, h.photo);

        h.date.setText(DateUtil.getFormattedDate(photo.date));

        h.root.setOnClickListener(v -> {
            if (onClickItemListener != null) onClickItemListener.onClickItem(photo);
        });

        h.root.setOnLongClickListener(v -> {
            // Confirm delete image dialog
            new AlertDialog.Builder(context)
                    .setTitle(R.string.confirm_delete_image)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        if (onDeleteItemListener != null) onDeleteItemListener.onDeleteItem(photo, position);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();

            return true;
        });

        // Load more
        if (getItemCount() >= 20 && position == getItemCount() - 4 && onLoadMoreListener != null) {
            onLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View root;
        ImageView photo;
        TextView date;

        public ViewHolder(@NonNull View root) {
            super(root);

            this.root = root;
            this.photo = root.findViewById(R.id.photo);
            this.date = root.findViewById(R.id.date);
        }
    }

}

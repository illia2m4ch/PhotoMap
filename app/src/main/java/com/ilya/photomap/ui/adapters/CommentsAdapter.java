package com.ilya.photomap.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.photomap.R;
import com.ilya.photomap.data.database.entities.Comment;
import com.ilya.photomap.data.database.entities.Photo;
import com.ilya.photomap.util.AppUtil;
import com.ilya.photomap.util.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    /**
     * On Delete Item Interface
     */
    public interface OnDeleteItemListener {
        void onDeleteItem(Comment com, int position);
    }

    private OnDeleteItemListener onDeleteItemListener;

    /**
     * On Click Item Interface
     */
    public interface OnClickItemListener {
        void onClickItem(Comment comment);
    }

    private OnClickItemListener onClickItemListener;

    /**
     * On Load More Interface
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private OnLoadMoreListener onLoadMoreListener;

    private List<Comment> comments;

    public CommentsAdapter() {
        comments = new ArrayList<>();
    }

    public CommentsAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public void loadComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComments(List<Comment> comments) {
        int itemCount = getItemCount();
        this.comments.addAll(comments);
        notifyItemRangeInserted(itemCount, comments.size());
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
        comments.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Comment comment = comments.get(position);
        Context context = h.root.getContext();

        h.text.setText(comment.text);
        h.date.setText(DateUtil.getFormattedDate(comment.date));

        h.root.setOnClickListener(v -> {
            if (onClickItemListener != null) onClickItemListener.onClickItem(comment);
        });

        h.root.setOnLongClickListener(v -> {
            // Confirm delete image dialog
            new AlertDialog.Builder(context)
                    .setTitle(R.string.confirm_delete_comment)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        if (onDeleteItemListener != null) onDeleteItemListener.onDeleteItem(comment, position);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();

            return false;
        });

        // Load more
        if (getItemCount() >= 20 && position == getItemCount() - 4 && onLoadMoreListener != null) {
            onLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View root;
        TextView text;
        TextView date;

        public ViewHolder(@NonNull View root) {
            super(root);

            this.root = root;
            this.text = root.findViewById(R.id.comment);
            this.date = root.findViewById(R.id.date);
        }
    }

}

package com.Projet.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.Projet.forum.databinding.CommentItemBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    public interface OnCommentActionListener {
        void onDeleteClick(Comment comment);
        void onEditClick(Comment comment); // ✅ Added
    }

    private final Context context;
    private List<Comment> comments;
    private final boolean isAdmin;
    private final String currentUserId;
    private final OnCommentActionListener listener;

    public CommentAdapter(Context context, List<Comment> comments, boolean isAdmin, String currentUserId, OnCommentActionListener listener) {
        this.context = context;
        this.comments = (comments != null) ? comments : new ArrayList<>();
        this.isAdmin = isAdmin;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding binding = CommentItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.binding.textViewAuthorName.setText(comment.getAuthor() != null ? comment.getAuthor().getName() : "Utilisateur");
        holder.binding.textViewCommentContent.setText(comment.getContent());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        holder.binding.textViewCommentTime.setText(sdf.format(new Date(comment.getTimestamp())));

        boolean isOwner = comment.getAuthor() != null && comment.getAuthor().getId().equals(currentUserId);

        // Delete: Admin or Owner
        holder.binding.buttonDelete.setVisibility((isAdmin || isOwner) ? View.VISIBLE : View.GONE);
        holder.binding.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(comment));

        // Edit: Owner Only
        holder.binding.buttonEdit.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        holder.binding.buttonEdit.setOnClickListener(v -> listener.onEditClick(comment));
    }

    @Override
    public int getItemCount() { return comments.size(); }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        CommentItemBinding binding;
        CommentViewHolder(CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

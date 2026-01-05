package com.Projet.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip; // ✅ Added
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private List<Post> postList = new ArrayList<>();
    private final boolean isAdmin;
    private final String currentUserId;
    private final OnPostActionListener listener;

    public interface OnPostActionListener {
        void onCommentClick(Post post);
        void onLikeClick(Post post);
        void onDeleteClick(Post post);
    }

    public PostAdapter(Context context,
                       List<Post> postList,
                       boolean isAdmin,
                       String currentUserId,
                       OnPostActionListener listener) {
        this.context = context;
        this.postList = postList != null ? postList : new ArrayList<>();
        this.isAdmin = isAdmin;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // --- Author Info ---
        holder.tvAuthorName.setText(post.getAuthor() != null ? post.getAuthor().getName() : "Utilisateur inconnu");

        // ✅ FEATURE 1: TOP CONTRIBUTOR BADGE LOGIC
        boolean isAuthorAdmin = post.getAuthor() != null && post.getAuthor().isAdmin();
        boolean isHighlyLiked = post.getLikeCount() >= 5;
        holder.ivBadge.setVisibility((isAuthorAdmin || isHighlyLiked) ? View.VISIBLE : View.GONE);

        // ✅ FEATURE 2: CATEGORY LOGIC
        if (post.getCategory() != null && !post.getCategory().isEmpty()) {
            holder.chipCategory.setText(post.getCategory());
            holder.chipCategory.setVisibility(View.VISIBLE);
        } else {
            holder.chipCategory.setVisibility(View.GONE);
        }

        // --- Date Formatting ---
        if (post.getTimestamp() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault());
            holder.tvPostDate.setText(sdf.format(new Date(post.getTimestamp())));
        } else {
            holder.tvPostDate.setText("");
        }

        // --- Content ---
        holder.tvPostTitle.setText(post.getTitle() != null ? post.getTitle() : "");
        holder.tvPostContent.setText(post.getContent() != null ? post.getContent() : "");

        // --- Likes Logic ---
        holder.btnLike.setText(String.valueOf(post.getLikeCount()));
        boolean alreadyLiked = post.isLikedBy(currentUserId);
        holder.btnLike.setIconResource(alreadyLiked
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);

        holder.btnLike.setOnClickListener(v -> {
            if (currentUserId == null || currentUserId.isEmpty()) {
                Toast.makeText(context, "Connectez-vous pour liker", Toast.LENGTH_SHORT).show();
                return;
            }
            if (post.isLikedBy(currentUserId)) {
                Toast.makeText(context, "Vous avez déjà aimé ce post", Toast.LENGTH_SHORT).show();
            } else {
                if (listener != null) listener.onLikeClick(post);
            }
        });

        // --- Comments Logic ---
        int commentCount = post.getComments() != null ? post.getComments().size() : 0;
        holder.btnCommentCount.setText(String.valueOf(commentCount));
        holder.btnCommentCount.setOnClickListener(v -> {
            if (listener != null) listener.onCommentClick(post);
        });

        // --- Delete Logic ---
        boolean isAuthor = post.getAuthor() != null && post.getAuthor().getId().equals(currentUserId);
        if (isAdmin || isAuthor) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(post);
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    public void updatePosts(List<Post> newPosts) {
        postList.clear();
        if (newPosts != null) postList.addAll(newPosts);
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvAuthorName, tvPostTitle, tvPostContent, tvPostDate;
        MaterialButton btnLike, btnCommentCount, btnDelete;
        ShapeableImageView ivAuthorAvatar;
        ImageView ivBadge;
        Chip chipCategory; // ✅ Added reference

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.textViewAuthorName);
            tvPostDate = itemView.findViewById(R.id.textViewPostDate);
            tvPostTitle = itemView.findViewById(R.id.textViewPostTitle);
            tvPostContent = itemView.findViewById(R.id.textViewPostContent);
            btnLike = itemView.findViewById(R.id.buttonLike);
            btnCommentCount = itemView.findViewById(R.id.buttonCommentCount);
            btnDelete = itemView.findViewById(R.id.buttonDelete);
            ivAuthorAvatar = itemView.findViewById(R.id.imageViewAuthorAvatar);
            ivBadge = itemView.findViewById(R.id.imageViewBadge);
            chipCategory = itemView.findViewById(R.id.chipCategory); // ✅ Initialize
        }
    }
}

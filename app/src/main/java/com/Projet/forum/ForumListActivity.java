package com.Projet.forum;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Projet.forum.databinding.ActivityForumListBinding;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumListActivity extends AppCompatActivity {

    private ActivityForumListBinding binding;
    private PostAdapter adapter;
    private ListenerRegistration postsListener;
    private ListenerRegistration notificationListener;
    private String currentUserId;
    private String selectedCategory = "Tous";

    // ✅ Theme Colors
    private final int GOLD_COLOR = Color.parseColor("#FFD700");
    private final int TEAL_COLOR = Color.parseColor("#008080");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForumListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ UI Style: Teal Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Forum PetConnect");
        }

        // Get User Info
        User currentUser = FirebaseStorageHelper.getInstance().getCurrentUser();
        currentUserId = (currentUser != null) ? currentUser.getId() : "";
        boolean isAdmin = (currentUser != null) && currentUser.isAdmin();

        setupNotificationChannel();

        adapter = new PostAdapter(
                this,
                null,
                isAdmin,
                currentUserId,
                new PostAdapter.OnPostActionListener() {
                    @Override
                    public void onCommentClick(Post post) {
                        Intent intent = new Intent(ForumListActivity.this, ForumDetailActivity.class);
                        intent.putExtra("postId", post.getPostId());
                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(Post post) {
                        FirebaseStorageHelper.getInstance().deletePost(post.getPostId(), success -> {
                            if (success) Toast.makeText(ForumListActivity.this, "Post supprimé", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onLikeClick(Post post) {
                        if (currentUserId.isEmpty()) return;

                        FirebaseFirestore.getInstance()
                                .collection("posts")
                                .document(post.getPostId())
                                .update("likedUserIds", FieldValue.arrayUnion(currentUserId))
                                .addOnSuccessListener(aVoid -> {
                                    if (post.getAuthor() != null) {
                                        notifyAuthorInFirestore(post.getAuthor().getId(),
                                                "Nouveau Like !",
                                                "Quelqu'un a aimé votre post : " + post.getTitle());
                                    }
                                });
                    }
                }
        );

        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPosts.setAdapter(adapter);

        // ✅ UI Style: Small, Black & Gold Filters
        setupCategoryFilters();

        // ✅ UI Style: Gold FAB with Black Icon
        binding.fabNewPost.setOnClickListener(v ->
                startActivity(new Intent(this, NewPostActivity.class))
        );
    }

    private void notifyAuthorInFirestore(String authorId, String title, String message) {
        if (authorId == null || authorId.equals(currentUserId)) return;

        Map<String, Object> notification = new HashMap<>();
        notification.put("targetUserId", authorId);
        notification.put("senderId", currentUserId);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("isRead", false);

        FirebaseFirestore.getInstance().collection("notifications")
                .add(notification)
                .addOnSuccessListener(doc -> Log.d("NOTIF", "Notification recorded for: " + authorId));
    }

    private void startListeningForMyNotifications() {
        if (currentUserId.isEmpty()) return;

        notificationListener = FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("targetUserId", currentUserId)
                .whereEqualTo("isRead", false)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            String title = dc.getDocument().getString("title");
                            String msg = dc.getDocument().getString("message");
                            showLocalPopup(title, msg);
                            dc.getDocument().getReference().update("isRead", true);
                        }
                    }
                });
    }

    private void showLocalPopup(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "forum_notifications")
                .setSmallIcon(R.drawable.ic_home)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "forum_notifications", "Forum Alerts", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startListeningForPosts();
        startListeningForMyNotifications();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (postsListener != null) postsListener.remove();
        if (notificationListener != null) notificationListener.remove();
    }

    private void setupCategoryFilters() {
        // ✅ Style for "Tous": Black text, Gold background, 12sp font
        binding.chipAll.setTextColor(Color.BLACK);
        binding.chipAll.setChipBackgroundColor(ColorStateList.valueOf(GOLD_COLOR));
        binding.chipAll.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        binding.chipAll.setChipMinHeight(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));

        binding.chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedCategory = "Tous";
                binding.chipAll.setChipBackgroundColor(ColorStateList.valueOf(GOLD_COLOR));
                startListeningForPosts();
            } else {
                binding.chipAll.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
            }
        });

        FirebaseFirestore.getInstance().collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        if (name != null) addFilterChip(name);
                    }
                });
    }

    /**
     * ✅ UI: Compact Tags, Black Text, White/Gold logic
     */
    private void addFilterChip(String categoryName) {
        Chip chip = new Chip(this);
        chip.setText(categoryName);
        chip.setCheckable(true);
        chip.setClickable(true);

        // Aesthetics
        chip.setTextColor(Color.BLACK);
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        // Compact Size (30dp)
        int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        chip.setChipMinHeight(heightPx);

        chip.setChipStrokeWidth(0);
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));

        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedCategory = categoryName;
                chip.setChipBackgroundColor(ColorStateList.valueOf(GOLD_COLOR));
                startListeningForPosts();
            } else {
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
            }
        });

        binding.chipGroupCategories.addView(chip);
    }

    private void startListeningForPosts() {
        if (postsListener != null) postsListener.remove();

        Query query = FirebaseFirestore.getInstance().collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        if (!selectedCategory.equals("Tous")) {
            query = query.whereEqualTo("category", selectedCategory);
        }

        postsListener = query.addSnapshotListener((value, error) -> {
            if (error != null) return;
            List<Post> posts = new ArrayList<>();
            if (value != null) {
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Post p = doc.toObject(Post.class);
                    if (p != null) posts.add(p);
                }
            }
            adapter.updatePosts(posts);
        });
    }
}

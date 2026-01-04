package com.Projet.forum;import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties // ✅ Prevents crashes/warnings from old 'imageUrl' fields in DB
public class Post {

    private String postId;
    private User author;
    private String title;
    private String content;
    private long timestamp;
    private List<String> likedUserIds;
    private List<Comment> comments;

    // No-argument constructor required for Firestore
    public Post() {
        this.comments = new ArrayList<>();
        this.likedUserIds = new ArrayList<>();
    }

    public Post(String postId, User author, String title, String content, long timestamp) {
        this.postId = postId;
        this.author = author;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.comments = new ArrayList<>();
        this.likedUserIds = new ArrayList<>();
    }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // ✅ FIXED: Calculate count from the list size.
    // This ensures the number displayed is always accurate to the data.
    public int getLikeCount() {
        return getLikedUserIds().size();
    }

    // Firestore needs this to map the field if it exists, but we don't use it for logic
    public void setLikeCount(int count) { /* ignored */ }

    public List<String> getLikedUserIds() {
        if (likedUserIds == null) likedUserIds = new ArrayList<>();
        return likedUserIds;
    }
    public void setLikedUserIds(List<String> likedUserIds) { this.likedUserIds = likedUserIds; }

    public List<Comment> getComments() {
        if (comments == null) comments = new ArrayList<>();
        return comments;
    }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    // ✅ FIXED: Check if user liked
    public boolean isLikedBy(String userId) {
        if (userId == null) return false;
        return getLikedUserIds().contains(userId);
    }
}

package com.Projet.forum;

import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.Objects;

@IgnoreExtraProperties
public class Comment {

    private String commentId;
    private String postId;
    private User author;
    private String content;
    private long timestamp;

    public Comment() {}

    public Comment(String commentId, String postId, User author, String content, long timestamp) {
        this.commentId = commentId;
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // ✅ REQUIRED for FieldValue.arrayRemove(comment) to find the correct object in Firestore
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(commentId, comment.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }
}

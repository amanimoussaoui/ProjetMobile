package com.Projet.forum;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration; // Added
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FirebaseStorageHelper {

    private static FirebaseStorageHelper instance;

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private User currentUser;

    private FirebaseStorageHelper() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static FirebaseStorageHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseStorageHelper();
        }
        return instance;
    }

    /* =========================
       AUTH / USERS
       ========================= */

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void logout() {
        auth.signOut();
        currentUser = null;
    }

    public void addUser(User user, BooleanCallback callback) {
        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(v -> callback.onResult(true))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public void getUser(String uid, UserCallback callback) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) callback.onResult(doc.toObject(User.class));
                    else callback.onResult(null);
                })
                .addOnFailureListener(e -> callback.onResult(null));
    }

    public interface UserCallback {
        void onResult(User user);
    }

    /* =========================
       POSTS (REAL-TIME UPDATED)
       ========================= */

    public interface PostsCallback {
        void onResult(List<Post> posts);
    }

    public interface PostCallback {
        void onResult(Post post);
    }

    public interface BooleanCallback {
        void onResult(boolean success);
    }

    /**
     * NEW: Real-time listener for posts.
     * This ensures the list updates automatically when likes or comments change.
     */
    public ListenerRegistration listenToPosts(PostsCallback callback) {
        return db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        callback.onResult(new ArrayList<>());
                        return;
                    }
                    if (snapshot != null) {
                        List<Post> posts = new ArrayList<>();
                        snapshot.forEach(doc -> posts.add(doc.toObject(Post.class)));
                        callback.onResult(posts);
                    }
                });
    }

    public void getAllPosts(PostsCallback callback) {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Post> posts = new ArrayList<>();
                    snapshot.forEach(doc -> posts.add(doc.toObject(Post.class)));
                    callback.onResult(posts);
                })
                .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
    }

    public void getPostById(String postId, PostCallback callback) {
        db.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(doc -> callback.onResult(doc.toObject(Post.class)))
                .addOnFailureListener(e -> callback.onResult(null));
    }

    public void addPost(Post post, BooleanCallback callback) {
        db.collection("posts")
                .document(post.getPostId())
                .set(post)
                .addOnSuccessListener(v -> callback.onResult(true))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public void updatePost(Post post, BooleanCallback callback) {
        db.collection("posts")
                .document(post.getPostId())
                .set(post)
                .addOnSuccessListener(v -> callback.onResult(true))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public void deletePost(String postId, BooleanCallback callback) {
        db.collection("posts")
                .document(postId)
                .delete()
                .addOnSuccessListener(v -> callback.onResult(true))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    /* =========================
       COMMENTS
       ========================= */

    public interface CommentsCallback {
        void onResult(List<Comment> comments);
    }

    public void getComments(String postId, CommentsCallback callback) {
        db.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Comment> comments = new ArrayList<>();
                    snapshot.forEach(doc -> comments.add(doc.toObject(Comment.class)));
                    callback.onResult(comments);
                })
                .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
    }

    public void addComment(String postId, Comment comment, BooleanCallback callback) {
        db.collection("posts")
                .document(postId)
                .collection("comments")
                .document(comment.getCommentId())
                .set(comment)
                .addOnSuccessListener(v -> callback.onResult(true))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public void deleteComment(String postId, String commentId, BooleanCallback callback) {
        db.collection("posts")
                .document(postId)
                .collection("comments")
                .document(commentId)
                .delete()
                .addOnSuccessListener(v -> callback.onResult(true))
                .addOnFailureListener(e -> callback.onResult(false));
    }
}

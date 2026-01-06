# 🗣️ PLAN DE MIGRATION MODULE FORUM

**Date**: 6 janvier 2026  
**Projet Source**: ProjetMobile-Forum  
**Package Source**: `com.Projet.forum`  
**Package Cible**: `com.example.petconnect.forum`

---

## 📊 ÉTAT ACTUEL

### **Configuration Gradle**
```kotlin
Package: com.Projet.forum
compileSdk: 36
minSdk: 29
targetSdk: 36
Java: 11
ViewBinding: ✅
```

### **Structure Code (20 classes)**
```
com.Projet.forum/
├── Activities (8)
│   ├── MainActivity.java (launcher simple)
│   ├── ForumListActivity.java ⭐ (liste posts + catégories + likes)
│   ├── ForumDetailActivity.java (détails + comments)
│   ├── NewPostActivity.java (création post)
│   ├── AddCommentActivity.java
│   ├── AdminForumActivity.java (modération)
│   ├── UserForumActivity.java (profil)
│   └── LoginActivity.java ⚠️ (auth locale à supprimer)
├── Adapters (2)
│   ├── PostAdapter.java
│   └── CommentAdapter.java
├── Models (3)
│   ├── Post.java
│   ├── Comment.java
│   └── User.java ⚠️ (incompatible)
├── Services (2)
│   ├── MyFirebaseMessagingService.java ⭐ (FCM)
│   └── FirebaseStorageHelper.java ⭐ (Firestore singleton)
├── Utils (3)
│   ├── ProfanityFilter.java ⭐
│   ├── TimeUtils.java
│   └── FileUtils.java
└── Fragments (2)
    ├── FirstFragment.java
    └── SecondFragment.java
```

---

## 🔴 PROBLÈMES CRITIQUES

### **1. Dependencies en Doublon**

#### Problème
```gradle
// build.gradle ACTUEL (Forum)
dependencies {
    // ❌ Glide déclaré 2 fois
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // ❌ Firebase BOM déclaré 2 fois
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
}
```

#### Solution
```gradle
// build.gradle NETTOYÉ
dependencies {
    // ✅ Une seule version
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    
    // ✅ Version unifiée
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
}
```

**Effort**: 15min

---

### **2. User Model Incompatible**

#### Problème
```java
// User.java ACTUEL (Forum)
public class User {
    private String id;
    private String name;
    private String email;
    private String password; // ⚠️ Stocké en clair!
    private boolean isAdmin;
    private String fcmToken;
    
    // Auth locale dans LoginActivity
}
```

#### Solution
```java
// User.java UNIFIÉ (Core)
public class User {
    private String id;              // Firebase Auth UID
    private String name;
    private String email;
    private String photoUrl;
    private String fcmToken;        // Pour notifications
    private boolean isAdmin;
    private long createdAt;
    
    // ❌ PAS de password (Firebase Auth)
}
```

**Actions**:
1. Supprimer `LoginActivity.java`
2. Supprimer champ `password` de User
3. Migrer vers Firebase Auth centralisé
4. Utiliser `AuthViewModel` du module auth

**Effort**: 3h

---

### **3. Auth Locale à Remplacer**

#### Problème
```java
// LoginActivity.java (à supprimer)
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Auth manuelle avec password en clair
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password) // ⚠️ INSECURE
            .get()
            .addOnSuccessListener(...)
    }
}
```

#### Solution
```kotlin
// LoginFragment.kt (module-auth)
class LoginFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    
    private fun login() {
        authViewModel.login(email, password) { result ->
            when(result) {
                is Result.Success -> {
                    // Navigation vers ForumListFragment
                    findNavController().navigate(R.id.action_login_to_forum)
                }
                is Result.Error -> {
                    // Afficher erreur
                }
            }
        }
    }
}
```

**Effort**: 2h

---

## ✅ CODE À RÉUTILISER

### **1. MyFirebaseMessagingService** ⭐

#### Code Source
```java
// MyFirebaseMessagingService.java (Forum)
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = "";
        String body = "";

        // Case 1: Standard Notification
        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
        }
        // Case 2: Data Message (User-to-User)
        else if (message.getData().size() > 0) {
            title = message.getData().get("title");
            body = message.getData().get("body");
        }

        if (title != null && !title.isEmpty()) {
            sendVisualNotification(title, body);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM", "New token: " + token);
        
        // Save token to Firestore
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .update("fcmToken", token);
    }
    
    private void sendVisualNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = 
            new NotificationCompat.Builder(this, "forum_notifications")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }
}
```

#### Migration
```
Source: ProjetMobile-Forum/MyFirebaseMessagingService.java
Cible: PetConnect/core/src/main/java/com/example/petconnect/core/services/NotificationService.java

Modifications:
1. Renommer package → com.example.petconnect.core.services
2. Renommer classe → NotificationService
3. Rendre configurable (channel ID, icon)
4. Ajouter gestion multi-modules (Events, Forum, Shop)
```

**Effort**: 2h

---

### **2. FirebaseStorageHelper** ⭐

#### Code Source
```java
// FirebaseStorageHelper.java (Forum)
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
    
    // Méthodes Forum
    public void addPost(Post post, BooleanCallback callback) { ... }
    public void getPosts(String category, PostsCallback callback) { ... }
    public void addComment(String postId, Comment comment, BooleanCallback callback) { ... }
    public void likePost(String postId, String userId, BooleanCallback callback) { ... }
    public void deletePost(String postId, BooleanCallback callback) { ... }
}
```

#### Fusion avec FirebaseService (Produit)
```java
// FirebaseManager.java UNIFIÉ (Core)
public class FirebaseManager {
    private static FirebaseManager instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final FirebaseStorage storage;
    
    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    
    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }
    
    // Getters
    public FirebaseFirestore getDb() { return db; }
    public FirebaseAuth getAuth() { return auth; }
    public FirebaseStorage getStorage() { return storage; }
    
    // Methods from FirebaseStorageHelper (Forum)
    public void addPost(Post post, OnSuccessListener<Void> listener) { ... }
    public void getPosts(String category, OnSuccessListener<List<Post>> listener) { ... }
    
    // Methods from FirebaseService (Produit)
    public void addProduct(Product product, OnSuccessListener<Void> listener) { ... }
    public void getProducts(OnSuccessListener<List<Product>> listener) { ... }
}
```

**Effort**: 4h

---

### **3. ProfanityFilter** ⭐

#### Code Source
```java
// ProfanityFilter.java (Forum)
public class ProfanityFilter {
    
    private static final String[] PROFANITY_LIST = {
        "badword1", "badword2", "badword3", // ...
    };
    
    public static boolean containsProfanity(String text) {
        if (text == null || text.isEmpty()) return false;
        
        String lowerText = text.toLowerCase();
        for (String word : PROFANITY_LIST) {
            if (lowerText.contains(word)) {
                return true;
            }
        }
        return false;
    }
    
    public static String filterProfanity(String text) {
        if (text == null || text.isEmpty()) return text;
        
        String filtered = text;
        for (String word : PROFANITY_LIST) {
            String replacement = "*".repeat(word.length());
            filtered = filtered.replaceAll(
                "(?i)" + word, // Case insensitive
                replacement
            );
        }
        return filtered;
    }
}
```

#### Migration
```
Source: ProjetMobile-Forum/ProfanityFilter.java
Cible: PetConnect/shared-ui/src/main/java/com/example/petconnect/ui/utils/ProfanityFilter.java

Usage:
- Module Forum: Filtrer posts et comments
- Module Events: Filtrer commentaires événements (futur)
- Module Adoption: Filtrer descriptions pets (futur)
```

**Effort**: 30min

---

### **4. Post & Comment Models**

#### Models Source
```java
// Post.java (Forum)
public class Post {
    private String postId;
    private User author;
    private String title;
    private String content;
    private String category;
    private long timestamp;
    private List<String> likedUserIds;
    private List<Comment> comments;
    
    // Getters/Setters
}

// Comment.java (Forum)
public class Comment {
    private String commentId;
    private User author;
    private String content;
    private long timestamp;
    
    // Getters/Setters
}
```

#### Migration
```
Source: ProjetMobile-Forum/Post.java, Comment.java
Cible: PetConnect/module-forum/src/main/java/com/example/petconnect/forum/domain/models/

Rester dans module-forum (spécifiques au forum, pas réutilisables)
```

**Effort**: 1h

---

## 📋 PLAN D'EXÉCUTION

### **Étape 1: Nettoyage (30min)**
```
1. Ouvrir ProjetMobile-Forum/app/build.gradle.kts
2. Retirer:
   - implementation("com.github.bumptech.glide:glide:4.15.1")
   - annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
   - implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
   - implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
3. Garder:
   - implementation("com.github.bumptech.glide:glide:4.16.0")
   - annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
   - implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
4. Gradle Sync
5. Tester build
```

### **Étape 2: Migration Core (4h)**
```
1. Copier MyFirebaseMessagingService → core/services/NotificationService
2. Copier FirebaseStorageHelper → core/firebase/
3. Fusionner avec FirebaseService → FirebaseManager
4. Tester Firebase Auth/Firestore/Storage
```

### **Étape 3: Migration Shared-UI (1h)**
```
1. Copier ProfanityFilter → shared-ui/utils/
2. Copier TimeUtils → shared-ui/utils/
3. Copier FileUtils → shared-ui/utils/
```

### **Étape 4: Créer Module Forum (3h)**
```
1. Créer module-forum/
2. Copier Models (Post, Comment)
3. Convertir Activities → Fragments:
   - ForumListActivity → ForumListFragment
   - ForumDetailActivity → ForumDetailFragment
   - NewPostActivity → NewPostFragment
4. Créer ViewModels:
   - ForumViewModel
   - PostDetailViewModel
5. Créer Repository:
   - ForumRepository
```

### **Étape 5: Auth Migration (2h)**
```
1. Supprimer LoginActivity.java
2. Modifier User model (retirer password)
3. Migrer données Firestore users:
   - Créer Firebase Auth users
   - Copier id, name, email, isAdmin, fcmToken
   - Supprimer passwords
4. Tester auth flow
```

### **Étape 6: Navigation (1h)**
```
1. Créer forum_nav_graph.xml
2. Intégrer dans nav_graph.xml principal
3. Tester navigation Events → Forum → Shop
```

### **Étape 7: Tests (1h)**
```
1. Tester création post
2. Tester ajout comment
3. Tester likes
4. Tester notifications FCM
5. Tester modération (ProfanityFilter)
6. Tester catégories
```

---

## 🎯 RÉSULTAT ATTENDU

### **Structure Finale**
```
PetConnect/
├── core/
│   ├── firebase/
│   │   └── FirebaseManager.java (fusionné)
│   └── services/
│       └── NotificationService.java (FCM)
├── shared-ui/
│   └── utils/
│       ├── ProfanityFilter.java
│       ├── TimeUtils.java
│       └── FileUtils.java
├── module-auth/
│   └── LoginFragment.kt (remplace LoginActivity)
└── module-forum/
    ├── ui/
    │   ├── ForumListFragment.kt
    │   ├── ForumDetailFragment.kt
    │   └── NewPostFragment.kt
    ├── viewmodels/
    │   ├── ForumViewModel.kt
    │   └── PostDetailViewModel.kt
    ├── domain/
    │   ├── models/
    │   │   ├── Post.java
    │   │   └── Comment.java
    │   └── repository/
    │       └── ForumRepository.kt
    └── adapters/
        ├── PostAdapter.kt
        └── CommentAdapter.kt
```

### **Fonctionnalités Préservées**
- ✅ Création posts avec catégories
- ✅ Commentaires multi-niveaux
- ✅ System de likes
- ✅ Notifications push (FCM)
- ✅ Modération (ProfanityFilter)
- ✅ Rôles Admin/User
- ✅ Timestamps formatés

### **Améliorations**
- ✅ Auth sécurisée (Firebase Auth)
- ✅ Architecture MVVM
- ✅ Navigation Component
- ✅ Repository pattern
- ✅ Code unifié avec autres modules

---

## ⚠️ POINTS D'ATTENTION

### **Données Firestore**
```
Collection: posts
Documents: {
  postId: string
  authorId: string (Firebase Auth UID)
  title: string
  content: string
  category: string
  timestamp: long
  likedUserIds: array<string>
}

Collection: comments (subcollection de posts)
Documents: {
  commentId: string
  authorId: string
  content: string
  timestamp: long
}
```

### **Migration Users**
```javascript
// Script Firestore pour migration
function migrateUsers() {
  const users = db.collection('users').get();
  
  users.forEach(user => {
    const data = user.data();
    
    // 1. Créer Firebase Auth user
    admin.auth().createUser({
      uid: data.id,
      email: data.email,
      displayName: data.name
    });
    
    // 2. Mettre à jour Firestore (retirer password)
    db.collection('users').doc(data.id).update({
      password: admin.firestore.FieldValue.delete()
    });
    
    // 3. Set custom claims (isAdmin)
    if (data.isAdmin) {
      admin.auth().setCustomUserClaims(data.id, { admin: true });
    }
  });
}
```

---

**Plan créé le**: 6 janvier 2026  
**Temps estimé total**: 12h  
**Complexité**: Moyenne-Élevée  
**Recommandation**: Commencer après modules Core + Auth

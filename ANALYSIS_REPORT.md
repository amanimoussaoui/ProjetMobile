# 🔍 RAPPORT D'ANALYSE APPROFONDIE - PROJETS PETCONNECT

**Date**: 6 janvier 2026  
**Analyseur**: AI Architecture Expert  
**Objectif**: Préparer l'intégration multi-modules

---

## 📊 SYNTHÈSE EXÉCUTIVE

| Métrique | Valeur |
|----------|--------|
| **Projets analysés** | 4 (Event ✅, Adoption ✅, Produit ✅, Forum ✅) |
| **Packages conflictuels** | 4 différents |
| **Code dupliqué estimé** | ~40% |
| **Dépendances communes** | 14 bibliothèques |
| **Temps d'intégration** | 22-28 heures |
| **Complexité** | Élevée 🔴 |

---

## 1️⃣ ANALYSE PAR PROJET

### **PROJET 1: PetConnect_Event** ✅

**Chemin**: `Desktop/DEVOPS/PetConnect_Event/`

#### Configuration Gradle
```kotlin
- Package: com.example.petconnect_event
- compileSdk: 34
- minSdk: 26
- targetSdk: 34
- Java: 1.8
```

#### Dépendances Clés
```kotlin
✅ Firebase BOM 32.7.0
✅ Glide 4.16.0
✅ ZXing 3.5.2
✅ Shimmer 0.5.0
✅ Material 1.11.0
```

#### Structure Code
```
event/
├── activity/ (4 classes)
│   ├── AddEditEventActivity
│   ├── EventDetailActivity
│   ├── EventListActivity
│   └── RegisterEventActivity
├── adapter/ (3 classes)
├── model/ (2 classes)
│   ├── Event.java
│   └── EventRegistration.java
├── repository/ (2 classes)
│   ├── EventRepository
│   └── EventRegistrationRepository
├── service/
├── util/ (6 utilitaires)
│   ├── QRCodeGenerator
│   ├── FavoritesManager
│   ├── AnimationManager
│   └── HapticFeedbackManager
└── broadcast/
```

#### Points Forts
- ✅ Architecture claire (repository pattern)
- ✅ Gestion QR code implémentée
- ✅ Animations et feedback haptique
- ✅ Firestore bien structuré

#### Points Faibles
- ❌ Pas de ViewModel (logique dans Activities)
- ❌ Pas de Navigation Component
- ❌ Initialisation Firebase répétée

---

### **PROJET 2: ProjetMobile-Adoption-module** ✅

**Chemin**: `Desktop/DEVOPS/ProjetMobile-Adoption-module/`

#### Configuration Gradle
```kotlin
- Package: com.example.petconnect
- compileSdk: 36
- minSdk: 29
- targetSdk: 36
- Java: 11
⚠️ ViewBinding activé
```

#### Dépendances Clés
```kotlin
✅ Firebase BOM 32.7.4
✅ Glide 4.16.0
✅ ViewPager2 1.0.0
⚠️ JavaMail 1.6.7 (doublon)
```

#### Structure Code
```
petconnect/
├── MainActivity.java (ViewPager2)
├── PetDetailsActivity.java
├── AdoptionFormActivity.java
├── PetPagerAdapter.java
├── Pet.java (model)
└── PetRepository.java
```

#### Points Forts
- ✅ ViewPager2 bien implémenté
- ✅ Animations de transition
- ✅ Recherche et filtres
- ✅ ViewBinding activé

#### Points Faibles
- ❌ Structure plate (tout dans package racine)
- ❌ Pas de séparation model/repository
- ❌ Initialisation Firestore directe dans Activity
- ⚠️ Dépendance JavaMail inutile (doublon)

---

### **PROJET 3: ProjetMobile-produit** ✅

**Chemin**: `Desktop/DEVOPS/ProjetMobile-produit/`

#### Configuration Gradle
```kotlin
- Package: com.petconnect (⚠️ différent!)
- compileSdk: 34
- minSdk: 26
- targetSdk: 36
- Kotlin + Java: 11
⚠️ ViewBinding activé
✅ BuildConfig activé
```

#### Dépendances Clés
```kotlin
✅ Firebase BOM 32.7.0
✅ Glide 4.16.0
✅ MPAndroidChart 3.1.0
✅ Stripe 20.37.2
✅ OkHttp 4.12.0
✅ Algolia Search 3.27.0
⚠️ Kotlin + Java mixte
```

#### Structure Code
```
petconnect/
├── MainActivity.java
├── activities/ (6 classes)
│   ├── ShopActivity
│   ├── ProductDetailActivity
│   ├── CartActivity
│   ├── OrderHistoryActivity
│   └── InvoiceActivity
├── adapters/ (4 adapters)
├── models/ (4 models)
│   ├── Product
│   ├── Order
│   ├── CartItem
│   └── Promotion
├── services/ (13 services!)
│   ├── FirebaseService ⭐
│   ├── CartService
│   ├── OrderService
│   ├── PaymentService
│   ├── NotificationService
│   ├── ProductSearchService
│   ├── RecommendationService
│   └── ... (6 autres)
└── dialogs/ (3 dialogs)
```

#### Points Forts
- ✅ Architecture bien organisée
- ✅ Services bien séparés
- ✅ **FirebaseService centralisé** ⭐
- ✅ Graphiques (MPAndroidChart)
- ✅ Paiements (Stripe)
- ✅ Recherche avancée (Algolia)

#### Points Faibles
- ❌ Package différent (`com.petconnect` vs `com.example.petconnect`)
- ❌ Mixte Kotlin/Java
- ❌ Trop de services (peut simplifier)
- ⚠️ Dépendances lourdes (Stripe, Algolia)

---

### **PROJET 4: ProjetMobile-Forum** ✅

**Chemin**: `Desktop/DEVOPS/ProjetMobile-Forum/`

#### Configuration Gradle
```kotlin
- Package: com.Projet.forum ⚠️
- compileSdk: 36
- minSdk: 29
- targetSdk: 36
- Java: 11
⚠️ ViewBinding activé
```

#### Dépendances Clés
```kotlin
⚠️ Firebase BOM 34.7.0 + 32.2.0 (DOUBLON!)
✅ Glide 4.16.0 + 4.15.1 (versions multiples)
✅ Navigation Component (Fragment + UI)
✅ Firebase Messaging 23.4.1
✅ Firebase Storage 21.0.1
✅ OkHttp 4.12.0
⚠️ Dépendances en doublon
```

#### Structure Code
```
forum/
├── MainActivity.java (simple launcher)
├── ForumListActivity.java ⭐ (liste posts)
├── ForumDetailActivity.java (détails + comments)
├── NewPostActivity.java (création post)
├── AddCommentActivity.java
├── AdminForumActivity.java
├── UserForumActivity.java
├── LoginActivity.java ⚠️ (auth locale)
├── adapters/
│   ├── PostAdapter.java
│   └── CommentAdapter.java
├── models/
│   ├── Post.java (postId, author, title, content, category, timestamp, likes, comments)
│   ├── Comment.java
│   └── User.java ⚠️ (id, name, email, password, isAdmin, fcmToken)
├── services/
│   ├── MyFirebaseMessagingService.java ⭐ (FCM notifications)
│   └── FirebaseStorageHelper.java ⭐ (Singleton Firestore)
└── utils/
    ├── ProfanityFilter.java ⭐
    ├── TimeUtils.java
    └── FileUtils.java
```

#### Points Forts
- ✅ **FirebaseStorageHelper** singleton pattern ⭐
- ✅ **Firebase Cloud Messaging** intégré
- ✅ **Modération** (ProfanityFilter)
- ✅ System de likes et comments
- ✅ Catégories de posts
- ✅ Admin/User roles
- ✅ Navigation Component partial
- ✅ ViewBinding activé

#### Points Faibles
- ❌ **Package totalement différent** (`com.Projet.forum`)
- ❌ **Firebase BOM en doublon** (34.7.0 + 32.2.0)
- ❌ **Glide en doublon** (4.16.0 + 4.15.1)
- ❌ **Auth locale** (LoginActivity avec password stocké en clair)
- ❌ **User model dupliqué** (différent des autres projets)
- ⚠️ Notifications channel hardcodé
- ⚠️ Pas de Repository pattern
- ⚠️ Logique métier dans Activities

#### Conflits Majeurs
1. **Package**: `com.Projet.forum` vs `com.example.petconnect`
2. **Firebase BOM**: 2 versions dans le même build.gradle (34.7.0 + 32.2.0)
3. **User Model**: Incompatible avec autres projets (password, isAdmin)
4. **Auth**: System d'authentification séparé (LoginActivity)
5. **Glide**: Versions multiples (4.15.1 + 4.16.0 + annotations)

---

## 2️⃣ ANALYSE DES CONFLITS

### **🔴 CONFLITS CRITIQUES**

#### A. Packages Différents
```
PetConnect_Event:       com.example.petconnect_event
ProjetMobile-Adoption:  com.example.petconnect
ProjetMobile-produit:   com.petconnect
ProjetMobile-Forum:     com.Projet.forum ⚠️
```

**Impact**: CRITIQUE - Empêche la compilation directe  
**Solution**: Unifier sous `com.example.petconnect`  
**Effort**: 6-8h (refactoring global + imports)

#### B. Versions SDK Incompatibles
```
Event:    minSdk 26, compileSdk 34, targetSdk 34
Adoption: minSdk 29, compileSdk 36, targetSdk 36
Produit:  minSdk 26, compileSdk 34, targetSdk 36
Forum:    minSdk 29, compileSdk 36, targetSdk 36
```

**Impact**: Moyen-Élevé  
**Solution**: Standardiser à `minSdk 26, compileSdk 36, targetSdk 36`  
**Effort**: 2h (tests de régression sur API 26-28)

#### C. Versions Java
```
Event:    Java 1.8
Adoption: Java 11
Produit:  Java 11
```

**Impact**: Moyen  
**Solution**: Migrer tout en Java 11

### **🟡 CONFLITS MODÉRÉS**

#### G. User Model Incompatible

**Projet Event**: Pas de User model  
**Projet Adoption**: Pas de User model  
**Projet Produit**: Pas de User model  
**Projet Forum**: User.java (id, name, email, password, isAdmin, fcmToken)

```java
// Forum User model - INCOMPATIBLE
public class User {
    private String id;
    private String name;
    private String email;
    private String password;  // ⚠️ Stocké en clair!
    private boolean isAdmin;
    private String fcmToken;
}
```

**Impact**: Élevé  
**Solution**: Créer User model unifié dans Core, retirer password local  
**Effort**: 3h (migration vers Firebase Auth centralisé)

#### H. Firebase Auth Dupliquée

**Projets Event/Adoption/Produit**: Firebase Auth centralisé  
**Projet Forum**: LoginActivity avec auth locale ⚠️

**Impact**: Élevé  
**Solution**: Supprimer LoginActivity Forum, utiliser auth centralisée  
**Effort**: 2h

#### I. Firebase Cloud Messaging

**Projet Forum**: MyFirebaseMessagingService ⭐ (notifications)  
**Autres projets**: Pas de FCM

**Impact**: Moyen  
**Solution**: Migrer MyFirebaseMessagingService vers module Core  
**Effort**: 2h

#### D. Firebase BOM
```
Event:    32.7.0
Adoption: 32.7.4
Produit:  32.7.0
Forum:    34.7.0 + 32.2.0 ⚠️ (DOUBLON dans même fichier!)
```

**Impact**: Élevé (Forum a 2 versions!)  
**Solution**: Unifier à 32.7.4, retirer doublons Forum  
**Effort**: 1h (nettoyage + tests Firebase)

#### E. Glide
```
Event:    4.16.0 ✅
Adoption: 4.16.0 ✅
Produit:  4.16.0 ✅
Forum:    4.16.0 + 4.15.1 ⚠️ (DOUBLON!)
```

**Impact**: Moyen  
**Solution**: Unifier à 4.16.0, retirer 4.15.1 du Forum  
**Effort**: 30min

#### F. Material Design
```
Event:    1.11.0
Adoption: 1.11.0
Produit:  1.10.0
```

**Impact**: Faible  
**Solution**: Unifier à 1.11.0

---

## 3️⃣ CODE DUPLIQUÉ IDENTIFIÉ

### **Services Firebase**

#### ProjetMobile-produit/services/FirebaseService.java ⭐
```java
public class FirebaseService {
    private static FirebaseFirestore db;
    private static FirebaseAuth auth;
    private static FirebaseStorage storage;
    
    public static FirebaseFirestore getDb() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }
    // ...
}
```

#### ProjetMobile-Forum/FirebaseStorageHelper.java ⭐
```java
public class FirebaseStorageHelper {
    private static FirebaseStorageHelper instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private User currentUser;
    
    public static FirebaseStorageHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseStorageHelper();
        }
        return instance;
    }
    // Méthodes: addUser, getPosts, addPost, addComment, etc.
}
```

**Action**: Fusionner les 2 services dans `core/firebase/FirebaseManager.java`  
**Effort**: 4h (unifier APIs, tests)

### **Firebase Messaging Service**

#### ProjetMobile-Forum/MyFirebaseMessagingService.java ⭐
```java
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        // Gestion notifications FCM
    }
}
```

**Action**: Migrer vers `core/services/NotificationService.java`  
**Effort**: 2h

### **QR Code Generator**

#### PetConnect_Event/util/QRCodeGenerator.java
```java
public class QRCodeGenerator {
    public static Bitmap generateQRCode(String content, int width, int height) {
        // Implémentation ZXing
    }
}
```

**Action**: Migrer vers `core/utils/QRCodeUtils.java`  
**Effort**: 1h

### **Models Dupliqués**

| Model | Event | Adoption | Produit | Forum |
|-------|-------|----------|---------|-------|
| User | ❌ | ❌ | ❌ | ✅ (incompatible) |
| Event | ✅ | ❌ | ❌ | ❌ |
| Pet | ❌ | ✅ | ❌ | ❌ |
| Product | ❌ | ❌ | ✅ | ❌ |
| Post | ❌ | ❌ | ❌ | ✅ |
| Comment | ❌ | ❌ | ❌ | ✅ |

**Action**: Créer models communs dans `core/domain/models/`  
**Effort**: 3h

### **Utils Communs**

| Util | Event | Adoption | Produit | Forum |
|------|-------|----------|---------|-------|
| TimeUtils | ✅ | ❌ | ❌ | ✅ |
| FileUtils | ❌ | ❌ | ❌ | ✅ |
| AnimationManager | ✅ | ❌ | ❌ | ❌ |
| HapticFeedback | ✅ | ❌ | ❌ | ❌ |

**Action**: Centraliser dans `shared-ui/utils/`  
**Effort**: 2h

---

## 4️⃣ DÉPENDANCES COMMUNES

### **À Mutualiser dans Core**

| Bibliothèque | Version cible | Usage |
|--------------|---------------|-------|
| Firebase BOM | 32.7.4 | Tous modules |
| Glide | 4.16.0 | Tous modules |
| ZXing | 3.5.2 | Events, Shop (QR) |
| Material | 1.11.0 | Tous modules |
| RecyclerView | 1.3.2 | Tous modules |
| CardView | 1.0.0 | Tous modules |
| ViewPager2 | 1.0.0 | Adoption |
| Navigation | 2.7.x | Tous modules |
| OkHttp | 4.12.0 | Shop, Forum |

### **Firebase Services**

| Service | Event | Adoption | Produit | Forum |
|---------|-------|----------|---------|-------|
| Auth | ✅ | ✅ | ✅ | ✅ |
| Firestore | ✅ | ✅ | ✅ | ✅ |
| Storage | ✅ | ❌ | ✅ | ✅ |
| Messaging | ❌ | ❌ | ❌ | ✅ |

**Action**: Centraliser dans Core module

### **Spécifiques aux Modules**

| Bibliothèque | Module | Raison |
|--------------|--------|--------|
| Shimmer | Events | Loading états |
| MPAndroidChart | Shop | Graphiques stats |
| Stripe | Shop | Paiements |
| Algolia | Shop | Recherche avancée |
| JavaMail | ❌ À retirer | Inutile (doublon) |

### **⚠️ Conflits de Versions à Résoudre**

| Bibliothèque | Versions trouvées | Action |
|--------------|-------------------|--------|
| Firebase BOM | 32.7.0, 32.7.4, 32.2.0, 34.7.0 | → 32.7.4 |
| Glide | 4.15.1, 4.16.0 | → 4.16.0 |
| Material | 1.10.0, 1.11.0 | → 1.11.0 |

---

## 5️⃣ ARCHITECTURE ACTUELLE VS CIBLE

### **État Actuel** 🔴
```
❌ Chaque projet = app complète
❌ Code dupliqué (Firebase, QR, Utils)
❌ Pas de Navigation Component
❌ Activities imbriquées
❌ Packages incohérents
❌ Initialisation Firebase répétée
```

### **Architecture Cible** ✅
```
✅ Module app principal (navigation host)
✅ Core module (Firebase, utils communs)
✅ Shared-UI (composants réutilisables)
✅ Feature modules indépendants
✅ Navigation Component global
✅ MVVM avec ViewModels
✅ Repository pattern uniformisé
```

---

## 6️⃣ PLAN DE MIGRATION RECOMMANDÉ

### **Phase 1: Préparation** (2h)
```
1. Backup projets
2. Créer branche Git
3. Créer structure modules vide
4. Configurer buildSrc
```

### **Phase 2: Module Core** (4h)
```
1. Copier FirebaseService de ProjetMobile-produit
2. Copier FirebaseStorageHelper de Forum
3. Fusionner en FirebaseManager.java
4. Migrer MyFirebaseMessagingService (FCM)
5. Migrer QRCodeGenerator
6. Créer User model unifié
7. Créer Extensions.kt
```

### **Phase 3: Module Shared-UI** (3h)
```
1. Unifier thème (colors, styles)
2. Créer composants communs
3. Extraire drawables/resources
4. Migrer utils communs (TimeUtils, FileUtils)
```

### **Phase 4: Module Auth** (3h)
```
1. Créer LoginFragment (remplacer toutes Activities auth)
2. Créer RegisterFragment
3. Créer AuthViewModel
4. Supprimer LoginActivity Forum
5. Implémenter Navigation
```

### **Phase 5: Modules Features** (10h)
```
1. Module Events (2h)
   - Convertir Activities → Fragments
   - Créer EventViewModel
   - Intégrer Navigation

2. Module Adoption (2h)
   - Adapter ViewPager2 avec Navigation
   - Créer AdoptionViewModel

3. Module Shop (3h)
   - Séparer services
   - Créer ShopViewModel
   - Gérer CartService

4. Module Forum (3h) ⭐ NOUVEAU
   - Convertir ForumListActivity → Fragment
   - Créer ForumViewModel
   - Intégrer notifications FCM
   - Migrer ProfanityFilter
   - Adapter Post/Comment models
```

### **Phase 6: Module App** (3h)
```
1. MainActivity avec NavHost
2. BottomNavigationView
3. Navigation globale (nav_graph)
4. Gestion splash/auth
```

---

## 7️⃣ RISQUES ET MITIGATION

| Risque | Probabilité | Impact | Mitigation |
|--------|-------------|--------|------------|
| Conflits Gradle | Élevé | Élevé | buildSrc centralisé |
| Perte de fonctionnalité | Moyen | Élevé | Tests E2E complets |
| Temps dépassé | Élevé | Moyen | Migration progressive |
| Bugs Firebase | Faible | Élevé | Tests unitaires |
| Conflits packages | Élevé | Élevé | Refactoring global |
| **Auth Forum incompatible** | **Élevé** | **Élevé** | **Migration Firebase Auth** |
| **User model conflits** | **Élevé** | **Élevé** | **Créer model unifié** |
| **Notifications FCM** | **Moyen** | **Moyen** | **Tests sur devices** |

---

## 8️⃣ CHECKLIST PRÉPARATOIRE

### Avant de commencer
- [ ] Backup complet des 4 projets
- [ ] Git repository initialisé
- [ ] Branche feature créée
- [ ] Android Studio à jour
- [ ] Gradle 8.1.0 installé
- [ ] Kotlin plugin à jour

### Vérifications techniques
- [ ] Firebase projects séparés ou unifiés?
- [ ] google-services.json prêts (4 projets)
- [ ] Firestore data backup (Events, Adoption, Shop, Forum)
- [ ] Clés API (Stripe, Algolia) sauvegardées
- [ ] FCM Server Key sauvegardée (Forum)
- [ ] Test notifications FCM sur devices
- [ ] Vérifier User data Forum (migration auth)

---

## 9️⃣ MÉTRIQUES DE RÉUSSITE

### Critères de validation
```
✅ Gradle Sync sans erreur
✅ Build réussie (tous modules)
✅ Navigation fluide entre modules
✅ Firebase fonctionne (auth + firestore)
✅ Aucune régression fonctionnelle
✅ Code coverage > 60%
✅ Pas de code dupliqué
✅ Architecture documentée
```

---

## 🎯 RECOMMANDATIONS FINALES

### **Priorités Immédiates**
1. 🔴 **Nettoyer Forum dependencies** → Retirer Firebase BOM doublon, Glide doublon
2. 🔴 **Unifier les packages** → `com.example.petconnect` (4 projets!)
3. ✅ **Créer module Core** → Base solide avec FirebaseManager
4. ✅ **Migrer services Firebase** → FirebaseService + FirebaseStorageHelper → FirebaseManager
5. 🔴 **Migrer Auth Forum** → Supprimer LoginActivity, utiliser Firebase Auth
6. ✅ **Standardiser SDK/Java** → Compatibilité

### **Quick Wins**
- Copier `FirebaseService.java` de ProjetMobile-produit vers Core
- Copier `FirebaseStorageHelper.java` de Forum vers Core
- Fusionner en `FirebaseManager.java` unifié
- Migrer `MyFirebaseMessagingService.java` vers Core
- Extraire `QRCodeGenerator` vers Core
- Unifier `colors.xml` et `strings.xml`
- Créer `Dependencies.kt` dans buildSrc
- **Nettoyer build.gradle Forum** (retirer doublons)

### **Points d'Attention**
- ⚠️ **Package renaming** va casser tous les imports (4 projets!)
- ⚠️ **Forum Auth** nécessite migration complète vers Firebase Auth
- ⚠️ **User model Forum** incompatible (password, isAdmin)
- ⚠️ **ViewPager2 Adoption** nécessite adaptation spéciale
- ⚠️ **Stripe/Algolia** rester dans module Shop uniquement
- ⚠️ **FCM Notifications** tester sur devices réels
- ⚠️ **Firebase BOM Forum** a 2 versions dans le même fichier!
- ⚠️ **Glide Forum** a 2 versions (4.15.1 + 4.16.0)

---

## 📞 PROCHAINES ACTIONS

**Prêt à commencer la Phase 2 (Module Core)**

Voulez-vous que je:
1. ✅ Crée les fichiers buildSrc/Dependencies
2. ✅ Crée le module Core
3. ✅ Migre FirebaseService
4. ✅ Commence l'implémentation?

---

**Rapport généré le**: 6 janvier 2026  
**Temps d'analyse**: ~25 minutes  
**Projets analysés**: 4/4 ✅ (Event ✅, Adoption ✅, Produit ✅, Forum ✅)  
**Recommandation**: **GO POUR MIGRATION** 🚀  

---

## 🎯 NOUVEAUTÉS MODULE FORUM

### **Fonctionnalités Uniques**
- ✅ **Firebase Cloud Messaging** (notifications push)
- ✅ **System de Likes** sur posts
- ✅ **Commentaires** multi-niveaux
- ✅ **Modération** avec ProfanityFilter
- ✅ **Catégories** de posts
- ✅ **Rôles** Admin/User
- ✅ **Notifications** en temps réel

### **Code à Réutiliser**
```java
// MyFirebaseMessagingService.java → core/services/
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        // Gestion notifications
    }
}

// FirebaseStorageHelper.java → À fusionner avec FirebaseService
public class FirebaseStorageHelper {
    public void addPost(Post post, BooleanCallback callback)
    public void getPosts(String category, PostsCallback callback)
    public void addComment(String postId, Comment comment, BooleanCallback callback)
    public void likePost(String postId, String userId, BooleanCallback callback)
}

// ProfanityFilter.java → shared-ui/utils/
public class ProfanityFilter {
    public static boolean containsProfanity(String text)
    public static String filterProfanity(String text)
}
```

### **Conflits à Résoudre d'Urgence**
```gradle
// ⚠️ ACTUEL - build.gradle Forum (PROBLÉMATIQUE)
dependencies {
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation(platform("com.google.firebase:firebase-bom:32.2.0")) // ❌ DOUBLON!
    implementation("com.github.bumptech.glide:glide:4.16.0") // ❌ DOUBLON!
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}

// ✅ CIBLE - Nettoyé
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.4")) // Version unifiée
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}
```

---

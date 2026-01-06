# 🚀 GUIDE D'INTÉGRATION COMPLÈTE - PETCONNECT MULTI-MODULES

## 📋 TABLE DES MATIÈRES
1. [État Actuel](#état-actuel)
2. [Architecture Cible](#architecture-cible)
3. [Plan d'Intégration](#plan-dintégration)
4. [Configuration Gradle](#configuration-gradle)
5. [Migration Module par Module](#migration-module-par-module)
6. [Navigation Globale](#navigation-globale)
7. [Checklist de Validation](#checklist-de-validation)

---

## 📊 ÉTAT ACTUEL

### Projets Existants
```
Desktop/DEVOPS/
├── PetConnect_Event/              ✅ Module Events
├── ProjetMobile-Adoption-module/  ✅ Module Adoption  
├── ProjetMobile-produit/          ✅ Module Shop
└── ProjetMobile-Forum/            ⚠️ À localiser

AndroidStudioProjects/
└── PetConnect/                    🔄 Projet intégré (partiel)
    ├── Events ✅
    ├── Adoption ✅
    ├── Shop ✅
    └── Forum ❌
```

### Problèmes Identifiés
- ❌ Tout dans un seul module `app/`
- ❌ Code dupliqué (FirebaseService, Models, Utils)
- ❌ Navigation mixte (Activities directes + Fragments)
- ❌ Pas de Navigation Component
- ❌ Architecture non uniformisée
- ❌ Dépendances en désordre

---

## 🎯 ARCHITECTURE CIBLE

```
PetConnect/
├── app/                           # Module principal
│   ├── MainActivity.kt            # Navigation host
│   ├── SplashActivity.kt         # Écran splash
│   └── navigation/
│       └── AppNavGraph.kt        # Navigation globale
│
├── core/                          # Module core (bibliothèque)
│   ├── data/
│   │   ├── repository/
│   │   └── remote/
│   │       └── FirebaseService.kt
│   ├── domain/
│   │   ├── models/               # Models communs
│   │   └── usecases/
│   └── utils/
│       ├── QRCodeGenerator.kt
│       ├── ImageLoader.kt
│       └── Extensions.kt
│
├── shared-ui/                     # Module UI commun
│   ├── components/
│   │   ├── CustomToolbar.kt
│   │   ├── PetCard.kt
│   │   ├── EmptyStateView.kt
│   │   └── LoadingView.kt
│   ├── theme/
│   │   ├── Colors.kt
│   │   ├── Typography.kt
│   │   └── Theme.kt
│   └── resources/
│       ├── values/
│       └── drawable/
│
├── module-auth/                   # Module authentification
│   ├── LoginFragment.kt
│   ├── RegisterFragment.kt
│   ├── ForgotPasswordFragment.kt
│   ├── ProfileFragment.kt
│   └── viewmodel/
│       └── AuthViewModel.kt
│
├── module-events/                 # Module événements
│   ├── EventListFragment.kt
│   ├── EventDetailFragment.kt
│   ├── AddEditEventFragment.kt
│   ├── RegisterEventFragment.kt
│   ├── viewmodel/
│   │   └── EventViewModel.kt
│   └── repository/
│       └── EventRepository.kt
│
├── module-adoption/               # Module adoption
│   ├── AdoptionListFragment.kt
│   ├── PetDetailsFragment.kt
│   ├── AdoptionFormFragment.kt
│   ├── viewmodel/
│   │   └── AdoptionViewModel.kt
│   └── repository/
│       └── PetRepository.kt
│
├── module-shop/                   # Module boutique
│   ├── ShopFragment.kt
│   ├── ProductDetailFragment.kt
│   ├── CartFragment.kt
│   ├── OrderHistoryFragment.kt
│   ├── InvoiceFragment.kt
│   ├── viewmodel/
│   │   └── ShopViewModel.kt
│   └── repository/
│       └── ProductRepository.kt
│
├── module-forum/                  # Module forum (nouveau)
│   ├── ForumListFragment.kt
│   ├── TopicDetailFragment.kt
│   ├── CreateTopicFragment.kt
│   ├── viewmodel/
│   │   └── ForumViewModel.kt
│   └── repository/
│       └── ForumRepository.kt
│
└── buildSrc/                      # Configuration Gradle centralisée
    └── src/main/kotlin/
        ├── Dependencies.kt
        ├── Versions.kt
        └── Configs.kt
```

---

## 📝 PLAN D'INTÉGRATION (ÉTAPES)

### **PHASE 1: PRÉPARATION** (1-2h)

#### Étape 1.1: Backup et Git
```bash
# Créer branche d'intégration
cd c:\Users\raeda\AndroidStudioProjects\PetConnect
git checkout -b feature/multi-module-integration

# Backup des projets sources
xcopy c:\Users\raeda\Desktop\DEVOPS\* c:\Users\raeda\Desktop\DEVOPS_BACKUP\ /E /I
```

#### Étape 1.2: Créer structure modules
```bash
# Créer dossiers modules
mkdir core
mkdir shared-ui
mkdir module-auth
mkdir module-events
mkdir module-adoption
mkdir module-shop
mkdir module-forum
mkdir buildSrc
```

#### Étape 1.3: Configuration buildSrc

**Créer**: `buildSrc/build.gradle.kts`
```kotlin
plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}
```

**Créer**: `buildSrc/src/main/kotlin/Dependencies.kt`
```kotlin
object Versions {
    const val kotlin = "1.9.0"
    const val gradle = "8.1.0"
    const val compileSdk = 36
    const val minSdk = 29
    const val targetSdk = 36
    
    // AndroidX
    const val appcompat = "1.6.1"
    const val core = "1.12.0"
    const val material = "1.11.0"
    const val navigation = "2.7.7"
    const val lifecycle = "2.7.0"
    
    // Firebase
    const val firebase = "32.7.4"
    const val firebaseAuth = "22.3.1"
    const val firebaseFirestore = "24.10.3"
    
    // Other
    const val glide = "4.16.0"
    const val zxing = "3.5.2"
    const val mpandroidchart = "3.1.0"
}

object Libs {
    // AndroidX
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.core}"
    const val material = "com.google.android.material:material:${Versions.material}"
    
    // Navigation
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    
    // Lifecycle
    const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    
    // Firebase
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebase}"
    const val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
    const val firebaseFirestore = "com.google.firebase:firebase-firestore-ktx"
    const val firebaseStorage = "com.google.firebase:firebase-storage-ktx"
    
    // Libraries
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val zxing = "com.google.zxing:core:${Versions.zxing}"
    const val mpandroidchart = "com.github.PhilJay:MPAndroidChart:v${Versions.mpandroidchart}"
}

object Modules {
    const val app = ":app"
    const val core = ":core"
    const val sharedUi = ":shared-ui"
    const val auth = ":module-auth"
    const val events = ":module-events"
    const val adoption = ":module-adoption"
    const val shop = ":module-shop"
    const val forum = ":module-forum"
}
```

---

### **PHASE 2: CRÉATION MODULE CORE** (2-3h)

#### Étape 2.1: Configuration core/build.gradle.kts

**Créer**: `core/build.gradle.kts`
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.petconnect.core"
    compileSdk = Versions.compileSdk
    
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Firebase
    api(platform(Libs.firebaseBom))
    api(Libs.firebaseAuth)
    api(Libs.firebaseFirestore)
    api(Libs.firebaseStorage)
    
    // Utils
    api(Libs.glide)
    api(Libs.zxing)
}
```

#### Étape 2.2: Migrer FirebaseService vers core

**Créer**: `core/src/main/java/com/example/petconnect/core/data/remote/FirebaseService.kt`
```kotlin
package com.example.petconnect.core.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseService {
    
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    
    fun initialize() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()
            storage = FirebaseStorage.getInstance()
        }
    }
    
    fun getAuth(): FirebaseAuth = auth ?: throw IllegalStateException("Firebase not initialized")
    
    fun getDb(): FirebaseFirestore = db ?: throw IllegalStateException("Firebase not initialized")
    
    fun getStorage(): FirebaseStorage = storage ?: throw IllegalStateException("Firebase not initialized")
    
    fun getCurrentUserId(): String? = auth?.currentUser?.uid
    
    fun isUserLoggedIn(): Boolean = auth?.currentUser != null
}
```

#### Étape 2.3: Migrer Models communs

**Créer**: `core/src/main/java/com/example/petconnect/core/domain/models/User.kt`
```kotlin
package com.example.petconnect.core.domain.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

**Créer**: `core/src/main/java/com/example/petconnect/core/domain/models/BaseModel.kt`
```kotlin
package com.example.petconnect.core.domain.models

interface BaseModel {
    val id: String
    val createdAt: Long
}
```

#### Étape 2.4: Utilitaires communs

**Créer**: `core/src/main/java/com/example/petconnect/core/utils/QRCodeGenerator.kt`
```kotlin
package com.example.petconnect.core.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap

object QRCodeGenerator {
    
    fun generateQRCode(
        content: String,
        width: Int = 512,
        height: Int = 512
    ): Bitmap? {
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.MARGIN] = 1
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}
```

**Créer**: `core/src/main/java/com/example/petconnect/core/utils/Extensions.kt`
```kotlin
package com.example.petconnect.core.utils

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}
```

---

### **PHASE 3: MODULE SHARED-UI** (2-3h)

#### Étape 3.1: Configuration shared-ui/build.gradle.kts

**Créer**: `shared-ui/build.gradle.kts`
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.petconnect.sharedui"
    compileSdk = Versions.compileSdk
    
    defaultConfig {
        minSdk = Versions.minSdk
    }
    
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(project(Modules.core))
    api(Libs.material)
    api(Libs.appcompat)
    api(Libs.glide)
}
```

#### Étape 3.2: Thème unifié

**Créer**: `shared-ui/src/main/res/values/colors.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Primary Colors -->
    <color name="primary">#00897B</color>
    <color name="primary_dark">#00695C</color>
    <color name="primary_light">#4DB6AC</color>
    <color name="accent">#FF6F00</color>
    
    <!-- Status Colors -->
    <color name="success">#4CAF50</color>
    <color name="warning">#FFC107</color>
    <color name="error">#F44336</color>
    <color name="info">#2196F3</color>
    
    <!-- Neutrals -->
    <color name="white">#FFFFFF</color>
    <color name="black">#000000</color>
    <color name="background_grey">#F5F5F5</color>
    <color name="divider">#E0E0E0</color>
    
    <!-- Text -->
    <color name="text_primary">#212121</color>
    <color name="text_secondary">#757575</color>
    <color name="text_hint">#BDBDBD</color>
</resources>
```

**Créer**: `shared-ui/src/main/res/values/themes.xml`
```xml
<resources>
    <style name="Theme.PetConnect" parent="Theme.Material3.Light.NoActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryVariant">@color/primary_dark</item>
        <item name="colorSecondary">@color/accent</item>
        <item name="android:statusBarColor">@color/primary</item>
    </style>
</resources>
```

#### Étape 3.3: Composants UI réutilisables

**Créer**: `shared-ui/src/main/res/layout/custom_toolbar.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.appbar.MaterialToolbar
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    android:background="@color/primary"
    android:elevation="4dp"
    app:navigationIcon="@drawable/ic_back"
    app:title="PetConnect"
    app:titleTextColor="@color/white" />
```

**Créer**: `shared-ui/src/main/res/layout/view_empty_state.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="24dp">
    
    <TextView
        android:id="@+id/tv_empty_icon"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="📭"
        android:textSize="64sp" />
    
    <TextView
        android:id="@+id/tv_empty_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Aucun élément"
        android:textSize="20sp"
        android:textStyle="bold"
        android:textColor="@color/text_primary"
        android:layout_marginTop="16dp" />
    
    <TextView
        android:id="@+id/tv_empty_message"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Il n'y a rien à afficher pour le moment"
        android:textSize="14sp"
        android:textColor="@color/text_secondary"
        android:gravity="center"
        android:layout_marginTop="8dp" />
    
</LinearLayout>
```

---

### **PHASE 4: MIGRATION MODULE AUTH** (2-3h)

#### Étape 4.1: Configuration module-auth/build.gradle.kts

**Créer**: `module-auth/build.gradle.kts`
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.petconnect.auth"
    compileSdk = Versions.compileSdk
    
    defaultConfig {
        minSdk = Versions.minSdk
    }
    
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(Modules.core))
    implementation(project(Modules.sharedUi))
    
    implementation(Libs.navigationFragment)
    implementation(Libs.viewmodel)
    implementation(Libs.livedata)
}
```

#### Étape 4.2: Convertir Activities en Fragments

**Migrer LoginActivity → LoginFragment**

Fichier actuel: `app/src/main/java/com/example/petconnect/modules/user/LoginActivity.java`

**Créer**: `module-auth/src/main/java/com/example/petconnect/auth/ui/LoginFragment.kt`
```kotlin
package com.example.petconnect.auth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.petconnect.auth.databinding.FragmentLoginBinding
import com.example.petconnect.auth.viewmodel.AuthViewModel
import com.example.petconnect.core.utils.showToast

class LoginFragment : Fragment() {
    
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> showLoading()
                is AuthState.Success -> navigateToHome()
                is AuthState.Error -> showError(state.message)
                else -> hideLoading()
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }
        
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
        
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgot_password)
        }
    }
    
    private fun navigateToHome() {
        findNavController().navigate(R.id.action_login_to_main)
    }
    
    private fun showError(message: String) {
        showToast(message)
        hideLoading()
    }
    
    private fun showLoading() {
        binding.btnLogin.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
    }
    
    private fun hideLoading() {
        binding.btnLogin.isEnabled = true
        binding.progressBar.visibility = View.GONE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

#### Étape 4.3: AuthViewModel

**Créer**: `module-auth/src/main/java/com/example/petconnect/auth/viewmodel/AuthViewModel.kt`
```kotlin
package com.example.petconnect.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petconnect.core.data.remote.FirebaseService

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    
    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState
    
    private val auth = FirebaseService.getAuth()
    private val db = FirebaseService.getDb()
    
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email et mot de passe requis")
            return
        }
        
        _authState.value = AuthState.Loading
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState.Success
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Erreur de connexion")
            }
    }
    
    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Tous les champs sont requis")
            return
        }
        
        _authState.value = AuthState.Loading
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                
                val userDoc = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )
                
                db.collection("users").document(uid).set(userDoc)
                    .addOnSuccessListener {
                        _authState.value = AuthState.Success
                    }
                    .addOnFailureListener { e ->
                        _authState.value = AuthState.Error(e.message ?: "Erreur création profil")
                    }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Erreur d'inscription")
            }
    }
    
    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Email requis")
            return
        }
        
        _authState.value = AuthState.Loading
        
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _authState.value = AuthState.Success
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Erreur envoi email")
            }
    }
}
```

---

### **PHASE 5: MIGRATION MODULES FEATURES** (6-8h)

*Même processus pour chaque module:*
- module-events
- module-adoption  
- module-shop
- module-forum

**Structure type d'un module feature:**
```
module-events/
├── build.gradle.kts
└── src/main/java/com/example/petconnect/events/
    ├── ui/
    │   ├── EventListFragment.kt
    │   ├── EventDetailFragment.kt
    │   └── AddEditEventFragment.kt
    ├── viewmodel/
    │   └── EventViewModel.kt
    ├── repository/
    │   └── EventRepository.kt
    └── model/
        └── Event.kt
```

---

### **PHASE 6: MODULE APP PRINCIPAL** (3-4h)

#### Étape 6.1: Configuration app/build.gradle.kts

**Modifier**: `app/build.gradle.kts`
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.petconnect"
    compileSdk = Versions.compileSdk
    
    defaultConfig {
        applicationId = "com.example.petconnect"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Modules
    implementation(project(Modules.core))
    implementation(project(Modules.sharedUi))
    implementation(project(Modules.auth))
    implementation(project(Modules.events))
    implementation(project(Modules.adoption))
    implementation(project(Modules.shop))
    implementation(project(Modules.forum))
    
    // Navigation
    implementation(Libs.navigationFragment)
    implementation(Libs.navigationUi)
}
```

#### Étape 6.2: Navigation globale

**Créer**: `app/src/main/res/navigation/nav_graph_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph_main"
    app:startDestination="@id/splashFragment">

    <!-- Splash -->
    <fragment
        android:id="@+id/splashFragment"
        android:name="com.example.petconnect.ui.splash.SplashFragment"
        android:label="Splash">
        <action
            android:id="@+id/action_splash_to_login"
            app:destination="@id/loginFragment"
            app:popUpTo="@id/splashFragment"
            app:popUpToInclusive="true" />
        <action
            android:id="@+id/action_splash_to_home"
            app:destination="@id/homeFragment"
            app:popUpTo="@id/splashFragment"
            app:popUpToInclusive="true" />
    </fragment>

    <!-- Auth Navigation -->
    <navigation
        android:id="@+id/auth_nav_graph"
        app:startDestination="@id/loginFragment">
        
        <fragment
            android:id="@+id/loginFragment"
            android:name="com.example.petconnect.auth.ui.LoginFragment"
            android:label="Login">
            <action
                android:id="@+id/action_login_to_register"
                app:destination="@id/registerFragment" />
            <action
                android:id="@+id/action_login_to_forgot_password"
                app:destination="@id/forgotPasswordFragment" />
            <action
                android:id="@+id/action_login_to_main"
                app:destination="@id/homeFragment"
                app:popUpTo="@id/auth_nav_graph"
                app:popUpToInclusive="true" />
        </fragment>
        
        <fragment
            android:id="@+id/registerFragment"
            android:name="com.example.petconnect.auth.ui.RegisterFragment" />
        
        <fragment
            android:id="@+id/forgotPasswordFragment"
            android:name="com.example.petconnect.auth.ui.ForgotPasswordFragment" />
    </navigation>

    <!-- Main Navigation -->
    <fragment
        android:id="@+id/homeFragment"
        android:name="com.example.petconnect.ui.home.HomeFragment"
        android:label="Home">
        
        <!-- Navigation vers Events -->
        <action
            android:id="@+id/action_home_to_events"
            app:destination="@id/events_nav_graph" />
        
        <!-- Navigation vers Adoption -->
        <action
            android:id="@+id/action_home_to_adoption"
            app:destination="@id/adoption_nav_graph" />
        
        <!-- Navigation vers Shop -->
        <action
            android:id="@+id/action_home_to_shop"
            app:destination="@id/shop_nav_graph" />
        
        <!-- Navigation vers Forum -->
        <action
            android:id="@+id/action_home_to_forum"
            app:destination="@id/forum_nav_graph" />
    </fragment>

    <!-- Events Sub-Graph -->
    <navigation
        android:id="@+id/events_nav_graph"
        app:startDestination="@id/eventListFragment">
        
        <fragment
            android:id="@+id/eventListFragment"
            android:name="com.example.petconnect.events.ui.EventListFragment" />
        
        <fragment
            android:id="@+id/eventDetailFragment"
            android:name="com.example.petconnect.events.ui.EventDetailFragment">
            <argument
                android:name="eventId"
                app:argType="string" />
        </fragment>
    </navigation>

    <!-- Adoption Sub-Graph -->
    <navigation
        android:id="@+id/adoption_nav_graph"
        app:startDestination="@id/adoptionListFragment">
        
        <fragment
            android:id="@+id/adoptionListFragment"
            android:name="com.example.petconnect.adoption.ui.AdoptionListFragment" />
        
        <fragment
            android:id="@+id/petDetailsFragment"
            android:name="com.example.petconnect.adoption.ui.PetDetailsFragment">
            <argument
                android:name="petId"
                app:argType="string" />
        </fragment>
    </navigation>

    <!-- Shop Sub-Graph -->
    <navigation
        android:id="@+id/shop_nav_graph"
        app:startDestination="@id/shopFragment">
        
        <fragment
            android:id="@+id/shopFragment"
            android:name="com.example.petconnect.shop.ui.ShopFragment" />
        
        <fragment
            android:id="@+id/cartFragment"
            android:name="com.example.petconnect.shop.ui.CartFragment" />
    </navigation>

    <!-- Forum Sub-Graph -->
    <navigation
        android:id="@+id/forum_nav_graph"
        app:startDestination="@id/forumListFragment">
        
        <fragment
            android:id="@+id/forumListFragment"
            android:name="com.example.petconnect.forum.ui.ForumListFragment" />
    </navigation>

</navigation>
```

#### Étape 6.3: MainActivity avec Navigation

**Créer**: `app/src/main/java/com/example/petconnect/MainActivity.kt`
```kotlin
package com.example.petconnect

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.petconnect.core.data.remote.FirebaseService
import com.example.petconnect.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialiser Firebase
        FirebaseService.initialize()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        
        navController = navHostFragment.navController
        
        // Setup bottom navigation
        binding.bottomNav.setupWithNavController(navController)
        
        // Masquer bottom nav sur certains écrans
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.forgotPasswordFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }
}
```

**Créer**: `app/src/main/res/layout/activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:defaultNavHost="true"
        app:navGraph="@navigation/nav_graph_main"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottom_nav"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@color/white"
        app:menu="@menu/bottom_nav_menu"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

---

### **PHASE 7: CONFIGURATION GRADLE RACINE** (1h)

#### Étape 7.1: settings.gradle.kts

**Modifier**: `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "PetConnect"

include(":app")
include(":core")
include(":shared-ui")
include(":module-auth")
include(":module-events")
include(":module-adoption")
include(":module-shop")
include(":module-forum")
```

#### Étape 7.2: build.gradle (racine)

**Modifier**: `build.gradle.kts`
```kotlin
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
```

---

## ✅ CHECKLIST D'INTÉGRATION

### Préparation
- [ ] Backup des projets existants
- [ ] Création branche Git `feature/multi-module-integration`
- [ ] Création structure dossiers modules

### Configuration
- [ ] buildSrc créé et configuré
- [ ] Dependencies.kt créé
- [ ] settings.gradle.kts mis à jour
- [ ] build.gradle racine mis à jour

### Module Core
- [ ] core/build.gradle.kts créé
- [ ] FirebaseService migré
- [ ] Models communs migrés
- [ ] Utilitaires migrés (QR, Extensions)

### Module Shared-UI
- [ ] shared-ui/build.gradle.kts créé
- [ ] Thème unifié créé
- [ ] Composants UI communs créés
- [ ] Drawables/ressources copiés

### Module Auth
- [ ] module-auth/build.gradle.kts créé
- [ ] LoginFragment créé
- [ ] RegisterFragment créé
- [ ] ForgotPasswordFragment créé
- [ ] AuthViewModel créé

### Modules Features
- [ ] module-events créé et migré
- [ ] module-adoption créé et migré
- [ ] module-shop créé et migré
- [ ] module-forum créé

### Module App
- [ ] app/build.gradle.kts mis à jour (dépendances modules)
- [ ] MainActivity convertie en Navigation Host
- [ ] nav_graph_main.xml créé
- [ ] BottomNavigationView configurée

### Tests
- [ ] Gradle Sync réussie
- [ ] Build réussie (gradlew clean build)
- [ ] Navigation Auth fonctionne
- [ ] Navigation Events fonctionne
- [ ] Navigation Adoption fonctionne
- [ ] Navigation Shop fonctionne
- [ ] Navigation Forum fonctionne
- [ ] Bottom Navigation fonctionne

### Finalisation
- [ ] Suppression code dupliqué
- [ ] Optimisation imports
- [ ] Tests end-to-end
- [ ] Merge dans develop

---

## 🎯 COMMANDES ESSENTIELLES

```bash
# Sync Gradle
./gradlew --refresh-dependencies

# Build tous les modules
./gradlew clean build

# Build module spécifique
./gradlew :module-events:build

# Installer sur device
./gradlew installDebug

# Vérifier dépendances
./gradlew :app:dependencies

# Analyser taille APK
./gradlew :app:bundleRelease
```

---

## 📊 TEMPS ESTIMÉ TOTAL

| Phase | Durée estimée |
|-------|---------------|
| Phase 1: Préparation | 1-2h |
| Phase 2: Module Core | 2-3h |
| Phase 3: Module Shared-UI | 2-3h |
| Phase 4: Module Auth | 2-3h |
| Phase 5: Modules Features | 6-8h |
| Phase 6: Module App | 3-4h |
| Phase 7: Configuration Gradle | 1h |
| **TOTAL** | **17-26h** |

---

## 🚀 PROCHAINES ÉTAPES

Voulez-vous que je commence par:
1. ✅ Créer les fichiers de configuration (buildSrc, Dependencies)
2. ✅ Migrer le module core
3. ✅ Créer le module shared-ui
4. ✅ Migrer le premier module (auth)

Ou préférez-vous un autre ordre d'exécution?

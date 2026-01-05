plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.petconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.petconnect"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildToolsVersion = "34.0.0"
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")

    // ⭐ SOLUTION 1 : Version mise à jour (recommandée) ⭐
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha13")

    // ⭐ SOLUTION 2 : Version originale + dépendance manquante ⭐
    // implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // implementation("androidx.constraintlayout:constraintlayout-core:1.0.4")

    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // RecyclerView & CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Firebase BOM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Glide pour les images
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Stripe pour les paiements
    implementation("com.stripe:stripe-android:20.37.2")

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Algolia Search
    implementation("com.algolia:algoliasearch-android:3.27.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // MPAndroidChart pour les graphiques
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Configuration simplifiée pour éviter les conflits
configurations.all {
    resolutionStrategy {
        // Force des versions spécifiques
        force("androidx.core:core-ktx:1.12.0")
        force("androidx.appcompat:appcompat:1.6.1")
        force("com.google.android.material:material:1.10.0")
        force("androidx.constraintlayout:constraintlayout:2.2.0-alpha13")

        // Exclure les anciennes bibliothèques
        exclude(group = "com.android.support")
    }
}
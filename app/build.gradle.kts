plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.petconnect"
    compileSdk {
        version = release(36)
    }
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.example.petconnect"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
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
    
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    // ============= PACKAGING CONFLICT RESOLUTION =============
    packaging {
        resources {
            excludes += setOf(
                "META-INF/NOTICE.md",
                "META-INF/LICENSE.md",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }
}

dependencies {
    // ============= ANDROIDX CORE =============
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // ============= LIFECYCLE & VIEWMODEL =============
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // ============= UI COMPONENTS =============
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    
    // ============= FIREBASE (MERGED FROM ALL MODULES) =============
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")

    // ============= IMAGE LOADING =============
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // ============= EVENT MODULE DEPENDENCIES =============
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // ============= SHOP MODULE DEPENDENCIES =============
    // Payment
    implementation("com.stripe:stripe-android:20.37.2")
    
    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Search
    implementation("com.algolia:algoliasearch-android:3.27.0")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // ============= EMAIL (ADOPTION MODULE) =============
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    // ============= TESTING =============
    testImplementation(libs.junit)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// ============= DEPENDENCY RESOLUTION STRATEGY =============
configurations.all {
    resolutionStrategy {
        force("androidx.core:core-ktx:1.12.0")
        force("androidx.appcompat:appcompat:1.6.1")
        force("com.google.android.material:material:1.11.0")
        force("androidx.constraintlayout:constraintlayout:2.1.4")
        exclude(group = "com.android.support")
    }
}

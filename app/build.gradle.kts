plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace   = "com.juyel.totka"
    compileSdk  = 35

    defaultConfig {
        applicationId          = "com.juyel.totka"
        minSdk                 = 24
        targetSdk              = 35
        versionCode            = 1
        versionName            = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ── Build-time constants (change TG_BOT_TOKEN before release) ──
        buildConfigField("String", "GAS_URL",
            "\"https://script.google.com/macros/s/AKfycbzrSAsUNxfsW2mtV7yQrOiPB6IloQ2XhMAgVhOmFXsQXk8KZSvD2UXYra-LnB0Id8vo1Q/exec\"")
        buildConfigField("String", "TG_BOT_TOKEN",
            "\"8712924439:AAEfoDN6HpXmba41aubNr6Pa1FD7taSiz48\"")              // ← replace with real token
        buildConfigField("String", "TG_CHAT_ID",
            "\"7929275539\"")
        buildConfigField("String", "MASTER_CSV",
            "\"https://docs.google.com/spreadsheets/d/1dJtuu61H_i1q_xL4--b4xsFtCVxzIi301bIDIAz1qdw/export?format=csv\"")
    }

    buildTypes {
        release {
            isMinifyEnabled   = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig  = true
        viewBinding  = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.0")

    // Network
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:ksp:4.16.0")

    // Room (local DB — quiz history, bookmarks)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")

    // WorkManager (daily reminder notifications)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Lottie (confetti / celebration animation)
    implementation("com.airbnb.android:lottie:6.4.0")

    // CSV parsing (for master/chapter sheets)
    implementation("com.opencsv:opencsv:5.9")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0")
}

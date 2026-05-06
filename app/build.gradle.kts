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

        buildConfigField("String", "GAS_URL", "\"https://script.google.com/macros/s/AKfycbzrSAsUNxfsW2mtV7yQrOiPB6IloQ2XhMAgVhOmFXsQXk8KZSvD2UXYra-LnB0Id8vo1Q/exec\"")
        buildConfigField("String", "TG_BOT_TOKEN", "\"8712924439:AAEfoDN6HpXmba41aubNr6Pa1FD7taSiz48\"")
        buildConfigField("String", "TG_CHAT_ID", "\"7929275539\"")
        buildConfigField("String", "MASTER_CSV", "\"https://docs.google.com/spreadsheets/d/1dJtuu61H_i1q_xL4--b4xsFtCVxzIi301bIDIAz1qdw/export?format=csv\"")

        // Room Schema এরর বন্ধ করার জন্য
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    // ⚡ অতি জরুরি: গ্রেডলকে বলে দেওয়া যে কোড 'kotlin' ফোল্ডারে আছে
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled   = true // প্রোডাকশন লেভেলে এটি true রাখা ভালো
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { 
        jvmTarget = "17" 
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    // Core & UI
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.0")

    // Network & JSON
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Image & Lottie
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:ksp:4.16.0")
    implementation("com.airbnb.android:lottie:6.4.0")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coroutines & Lifecycle
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")

    // Utilities
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.opencsv:opencsv:5.9")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0")
}

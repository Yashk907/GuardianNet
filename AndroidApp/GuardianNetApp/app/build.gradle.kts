plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.guardiannetapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.guardiannetapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        compose = true
    }
}

dependencies {

// ✅ Jetpack Compose BOM (Material 3, UI, Tooling, etc.)
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // ✅ Google Fonts + Material Icons
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // ✅ ViewModel + Hilt (DI)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    implementation("androidx.hilt:hilt-navigation-compose")
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")

    // ✅ Retrofit for Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // ✅ Jetpack Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.9.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")


    // ✅ Optional Tools
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")

    //maps

// Maps Compose
    implementation("com.google.maps.android:maps-compose:4.3.0")

    // Maps Utils (optional)
    implementation("com.google.maps.android:maps-utils-ktx:3.4.0")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    //location
    implementation ("com.google.android.gms:play-services-location:21.3.0")

    //websockets
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.socket:socket.io-client:2.1.0")
//    {
//        exclude group: "org.json", module: "json"
//    }



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
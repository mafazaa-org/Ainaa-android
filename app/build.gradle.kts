plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("kotlinx-serialization")
}

android {
    namespace = "com.mafazaa.ainaa"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mafazaa.ainaa"
        minSdk = 26
        targetSdk = 36
        versionCode = 10
        versionName = "v0.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
}

dependencies {
// Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.navigation.compose.jvmstubs)
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")

// Material
    implementation("com.google.android.material:material:1.6.0")

// Navigation
    implementation("androidx.navigation3:navigation3-runtime:1.0.0-alpha05")
    implementation("androidx.navigation3:navigation3-ui:1.0.0-alpha05")
    implementation(libs.androidx.navigation3.ui.android)

// Ktor
    implementation(platform("io.ktor:ktor-bom:3.2.2"))
    implementation("io.ktor:ktor-client-android")
    implementation("io.ktor:ktor-client-serialization")
    implementation("io.ktor:ktor-client-logging")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

// Koin for Android
    implementation("io.insert-koin:koin-android:4.1.0")
    implementation("io.insert-koin:koin-androidx-compose:4.1.0")
    implementation("io.insert-koin:koin-androidx-workmanager:4.1.0")

// Lifecycle
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.savedstate:savedstate:1.2.1")

// WorkManager
    implementation("androidx.work:work-runtime-ktx:2.10.3")
    //js engine
    implementation("org.mozilla:rhino:1.7.14")
    //gson
    implementation("com.google.code.gson:gson:2.10.1")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

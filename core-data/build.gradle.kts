plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")
}

android {
    namespace = "ru.point.core_data"
    compileSdk = 35

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    }
}

dependencies {
    implementation ("androidx.security:security-crypto:1.1.0-alpha03"){
        isTransitive = false
    }
    implementation ("com.google.crypto.tink:tink-android:1.6.1"){
        isTransitive = false
    }
    kapt("androidx.room:room-compiler:2.7.1")
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation(libs.bundles.navigation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.dagger)
    kapt(libs.kapt)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
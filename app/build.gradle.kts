plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
}

android {
    namespace = "ru.point.mealmaster"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.point.mealmaster"
        minSdk = 30
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
        viewBinding = true
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:fasting"))
    implementation(project(":feature:home"))
    implementation(project(":feature:meal"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:recipes"))


    implementation(libs.okHttp3)
    implementation(libs.jakewharton.retrofit)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    kapt(libs.kapt)
    implementation(libs.bundles.navigation)
    implementation(libs.dagger)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
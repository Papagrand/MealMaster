plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "ru.point.auth"
    compileSdk = 35

    defaultConfig {
        minSdk = 30

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

kotlin {
    sourceSets.all {
        languageSettings.enableLanguageFeature("ExplicitBackingFields")
    }
}

dependencies {

    implementation(project(":core"))
    implementation(project(":api"))

    kapt(libs.kapt)
    implementation(libs.retrofit)
    implementation(libs.dagger)
    implementation(libs.bundles.navigation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("com.cheonjaeung.powerwheelpicker.android:powerwheelpicker:1.0.0") {
        isTransitive = false
    }
    implementation("com.github.worker8:RadioGroupPlus:1.0.1") {
        isTransitive = false
    }
    testImplementation("io.mockk:mockk:1.13.3")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation(libs.junit)
    androidTestImplementation("io.mockk:mockk-android:1.13.3")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
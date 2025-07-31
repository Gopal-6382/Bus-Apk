plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.bus"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bus"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    configurations.all {
        resolutionStrategy {
            force ("androidx.fragment:fragment:1.5.7")
            force ("com.google.android.material:material:1.10.0")
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Enable debugging tools for the debug build
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase platform for unified versioning
    implementation(platform(libs.firebase.bom))
        implementation(platform(libs.firebase.bom.v3270))

    // Firebase services
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.core)
    implementation(libs.firebase.crashlytics)
    implementation(libs.play.services.auth)

    // AndroidX Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    

    implementation(libs.activity.ktx)
    implementation(libs.activity.ktx.v182)
    implementation (libs.firebase.firestore.v2412)

    // Lottie for animations
    implementation(libs.lottie)
    implementation(libs.lottie.compose)
    implementation(libs.room.common)
    implementation(libs.room.runtime)
    implementation(libs.navigation.runtime.android)
    implementation(libs.androidx.navigation.ui)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.room.compiler)
}

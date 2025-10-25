plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.mapb.gestfv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mapb.gestfv"
        minSdk = 26
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    // Add the dependency for the Firebase Authentication library
    implementation("com.firebaseui:firebase-ui-auth:9.0.0")
    // Declare the dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-perf:21.0.5")
    // Libreria Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    // Libreria Glide(para asignar url de imagenes, gifs etc.. a imageviews de forma sencilla)
    implementation("com.github.bumptech.glide:glide:4.13.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")

}
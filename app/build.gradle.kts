plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")

}


android {
    namespace = "com.example.musicplayer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 24
        targetSdk = 36
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
        viewBinding = true
    }

    // Add this to ignore duplicate META-INF files
    packagingOptions {
        resources {
            excludes += listOf(
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
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.junit.ktx)
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Unit tests [Junit]
    testImplementation("junit:junit:4.13.2")  // Unit test framework
    androidTestImplementation("androidx.test.ext:junit:1.2.1")

    // Instrumented tests
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Swipe refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //Send email
    implementation("com.sun.mail:android-mail:1.6.2")
    implementation("com.sun.mail:android-activation:1.6.2")

    // Storing Objects
    implementation(libs.gson)

    // Notifications
    implementation("androidx.media:media:1.3.1")

    //Google gmail API
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.api-client:google-api-client-android:1.34.1")
    implementation("com.google.api-client:google-api-client-gson:1.34.1")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
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

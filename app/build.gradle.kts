plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.parcelize")
//    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.weatherappandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherappandroid"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    val retrofitVersion = "2.9.0";
    val lifecycleVersion = "2.8.4";
    val glideVersion = "4.16.0";
    val GsonVersion = "2.10.0";
    val okHTTPversion = "4.12.0";

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    // retrofit2
//    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
//    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
//    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")
//    implementation("com.squareup.okhttp3:logging-interceptor:$okHTTPversion")
//    implementation("com.squareup.okhttp3:okhttp-urlconnection:$okHTTPversion")
//    implementation("com.squareup.okhttp3:okhttp:$okHTTPversion")
//    // Android lifecycle
//    implementation("androidx.lifecycle:lifecycle-common:$lifecycleVersion")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
//    // Gson
//    implementation("com.google.code.gson:gson:$GsonVersion")
//    // Swipe refresh
//    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
//    // ViewModel
//    implementation ("com.github.bumptech.glide:glide:$glideVersion")
//    implementation ("com.github.Dimezis.BlurView:version-2.0.3")


    //////////////    Android Studio Gradle Version Catalogs
    // Retrofit and OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.rxjava2)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.okhttp.urlconnection)

    // Android lifecycle
    implementation(libs.lifecycle.common)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Gson
    implementation(libs.gson)

    // Swipe refresh
    implementation(libs.swiperefreshlayout)

    // Glide
    implementation(libs.glide)

    // BlurView
    implementation ("com.github.Dimezis:BlurView:version-2.0.5")

}

kapt{
    correctErrorTypes = true
}
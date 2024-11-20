import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.plugin)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

val properties = Properties()
properties.load(rootProject.file("local.properties").inputStream())

android {
    namespace = "com.hexa.arti"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hexa.arti"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String","SERVER_URL",properties["SERVER_URL"] as String)
        buildConfigField("String","FAST_SERVER_URL",properties["FAST_SERVER_URL"] as String)
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.gridlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //hilt
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    //retrofit
    implementation(libs.bundles.network)
    implementation(libs.logging.interceptor)
    //viewmodel
    implementation(libs.viewmodel)
    implementation(libs.activity.ktx)
    //navigation
    implementation(libs.navigation.ktx)
    implementation(libs.navigation.ui)
//    implementation(libs.navigation.safe.args)
    //coroutine
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    //fragment
    implementation(libs.fragment.ktx)
    //glide
    implementation(libs.glide)
    //timber
    implementation(libs.timber)
    //ViewPager2
    implementation(libs.viewpager2)
    //drawerLayout
    implementation(libs.androidx.drawerlayout)
    //MediaPlayer
    implementation(libs.kotlinx.coroutines.android.v164)
    //datastore
    implementation(libs.datastore)
    //PieChart
    implementation(libs.piechart)
    //paging3
    implementation(libs.paging3)
    //AR
    implementation("io.github.sceneview:arsceneview:2.2.1")
}
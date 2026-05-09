import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.firebase.perf)
    id("androidx.room")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.abueltaweel"
    compileSdk { version = release(36) }

    bundle {
        language { enableSplit = false }
        density  { enableSplit = true  }
        abi      { enableSplit = true  }
    }

    defaultConfig {
        applicationId = "com.abueltaweel"
        minSdk = 26
        targetSdk = 36
        versionCode = 15
        versionName = "1.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        fun getProp(key: String): String =
            localProperties.getProperty(key) ?: System.getenv(key) ?: "placeholder_$key"

        buildConfigField("String", "SUPABASE_URL", "\"${getProp("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${getProp("SUPABASE_KEY")}\"")

        ndk { abiFilters += listOf("armeabi-v7a", "arm64-v8a") }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            firebaseCrashlytics {
                mappingFileUploadEnabled = false
                nativeSymbolUploadEnabled = false
            }
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            firebaseCrashlytics {
                mappingFileUploadEnabled = true
                nativeSymbolUploadEnabled = true
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    room { schemaDirectory("$projectDir/schemas") }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.bundles.navigation.compose)
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    implementation(platform(libs.bom))
    implementation(libs.bundles.supabase)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.firebase.analytics)

    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.media:media:1.6.0")
    implementation(libs.bundles.androidx.core)
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.network)
    implementation("com.github.msarhan:ummalqura-calendar:1.1.9")
    implementation(libs.bundles.maps.location)
    implementation(libs.bundles.image.loading)
    implementation(libs.bundles.others)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.media3.exoplayer)
    debugImplementation(libs.bundles.compose.debug)
}

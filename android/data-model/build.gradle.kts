/*
 * Created by Tomasz Kiljanczyk on 03/01/2022, 23:17
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 03/01/2022, 23:13
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.lyriccast.kotlin.quality)
}

android {
    defaultConfig {
        minSdk = 27
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.datamodel"
    compileSdk = 37
}

dependencies {
    // Submodules
    implementation(project(":data-transfer"))
    implementation(project(":common"))

    // Library dependencies
    implementation(libs.kotlinx.coroutines)

    // Hilt dependencies
    implementation(libs.hilt)
    ksp(libs.hiltCompiler)

    // Room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
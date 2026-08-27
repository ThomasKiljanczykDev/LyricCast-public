/*
 * Created by Tomasz Kiljanczyk on 03/01/2022, 23:17
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 03/01/2022, 23:13
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.navigationSafeArgs)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.googleServices)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.lyriccast.kotlin.quality)
}

android {
    val major = 2
    val minor = 0
    val patch = 0
    // used for hotfix, alpha, beta, etc. versions
    val revision = 2

    defaultConfig {
        applicationId = "dev.thomas_kiljanczyk.lyriccast"
        minSdk = 27
        targetSdk = 35

        // Versioning
        // Max version code is 2,100,000,000
        // Version code is calculated as follows:
        // revision - up to 99
        // patch - up to 999
        // minor - up to 999
        // major - up to 21
        versionCode = major * 100_000_000 + minor * 100_000 + patch * 100 + revision
        versionName = "$major.$minor.$patch${if (revision > 0) ".$revision" else ""}"

        testInstrumentationRunner = "dev.thomas_kiljanczyk.lyriccast.core.testing.LyricCastTestRunner"
        androidResources {
            localeFilters.addAll(
                listOf(
                    "en", "pl", "am", "de", "es", "fil", "fr", "in", "it", "ko", "pt", "sw", "vi",
                    "b+zh+Hans"
                )
            )
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
    namespace = "dev.thomas_kiljanczyk.lyriccast"
    compileSdk = 37
}

dependencies {
    // Submodules
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.dataTransfer)
    implementation(projects.core.datastoreProto)
    implementation(projects.core.database)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.session)
    implementation(projects.core.nearby)
    implementation(projects.core.cast)
    implementation(projects.core.playback)
    implementation(projects.core.sync)
    implementation(projects.core.tutorial)

    implementation(projects.feature.category.impl)
    implementation(projects.feature.main.impl)
    implementation(projects.feature.session.impl)
    implementation(projects.feature.setlist.impl)
    implementation(projects.feature.settings.impl)
    implementation(projects.feature.song.impl)

    // App dependencies
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.play.services.nearby)

    // Architecture Components
    implementation(libs.androidx.datastore)

    // AndroidX
    implementation(libs.android.material)
    implementation(libs.androidx.coreKtx)

    // Chromecast
    implementation(libs.google.castFramework)
    implementation(libs.androidx.mediaRouter)

    // Hilt
    implementation(libs.hilt)
    ksp(libs.hiltCompiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlyticsKtx)
    implementation(libs.firebase.analyticsKtx)

    // AndroidX Test - Instrumented testing
    androidTestImplementation(libs.androidx.test.coreKtx)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.androidx.test.extJunitKtx)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.androidx.test.espressoContrib) {
        // TODO: nice to have - workaround for protobuf-lite test issues, try to remove it in the future
        // Source: https://stackoverflow.com/questions/66154727/java-lang-nosuchmethoderror-no-static-method-registerdefaultinstance-with-fireb
        exclude(module = "protobuf-lite")
    }
    androidTestImplementation(libs.androidx.rules)

    // AndroidX Test - Hilt testing
    androidTestImplementation(libs.hiltTesting)
    kspAndroidTest(libs.hiltCompiler)

    // Test infrastructure submodules
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.dataTest)
    androidTestImplementation(projects.core.nearbyTest)
    androidTestImplementation(projects.core.castTest)

    // LeakCanary
//    debugImplementation(libs.squareup.leakCanary)

    // Compose dependencies
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.viewmodel)
    implementation(libs.androidx.compose.hilt)
    implementation(libs.androidx.compose.navigation)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Reorderable for drag and drop
    implementation(libs.reorderable)

    // Kotlinx collections immutable
    implementation(libs.kotlinx.collections.immutable)

    // Other dependencies
    implementation(libs.apache.commonsLang)
    implementation(libs.zip4j)

    coreLibraryDesugaring(libs.android.desugarJdkLibs)
}

/*
 * Created by Tomasz Kiljanczyk on 03/01/2022, 23:12
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 03/01/2022, 23:12
 */

plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
        consumerProguardFiles("consumer-rules.pro")
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.datatransfer"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    // Submodules
    implementation(projects.core.common)

    // Dependencies for local unit tests
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
}

/*
 * Created by Tomasz Kiljanczyk on 03/01/2022, 23:17
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 03/01/2022, 23:13
 */

plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.android.hilt)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.data"
}

dependencies {
    // Submodules
    implementation(projects.core.common)
    api(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.dataTransfer)
    api(projects.core.datastoreProto)

    // Library dependencies
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.datastore)

    // Dependencies for local unit tests
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}

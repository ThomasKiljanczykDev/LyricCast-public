plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.android.hilt)
    alias(libs.plugins.room)
}

android {
    defaultConfig {
        minSdk = 27
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.database"

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)

    implementation(libs.kotlinx.coroutines)

    api(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.google.truth)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.google.truth)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.test.coreKtx)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(projects.core.common)
    androidTestImplementation(projects.core.model)
}

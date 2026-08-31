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
    implementation(projects.core.common)
    api(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.dataTransfer)
    api(projects.core.datastoreProto)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.datastore)

    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}

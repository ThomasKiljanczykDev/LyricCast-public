plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.sync"
}

dependencies {
    api(projects.core.nearby)
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.session)
    implementation(projects.core.data)
    implementation(projects.core.dataTransfer)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(projects.core.nearbyTest)
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}

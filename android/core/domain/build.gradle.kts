plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.domain"
}

dependencies {
    implementation(projects.core.common)
    api(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.dataTransfer)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}

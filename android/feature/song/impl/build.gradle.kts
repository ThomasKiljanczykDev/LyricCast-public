plugins {
    alias(libs.plugins.lyriccast.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.feature.song.impl"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.cast)
    implementation(projects.core.playback)
    implementation(projects.core.tutorial)

    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.google.castFramework)

    testImplementation(libs.kotlinx.coroutines.test)
}

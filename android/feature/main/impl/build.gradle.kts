plugins {
    alias(libs.plugins.lyriccast.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.feature.main.impl"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.nearby)
    implementation(projects.core.sync)
    implementation(projects.core.cast)
    implementation(projects.core.dataTransfer)
    implementation(projects.core.tutorial)

    implementation(libs.androidx.compose.activity)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.coroutines)
}

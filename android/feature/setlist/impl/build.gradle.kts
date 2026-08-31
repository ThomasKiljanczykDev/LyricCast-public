plugins {
    alias(libs.plugins.lyriccast.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.cast)
    implementation(projects.core.playback)
    implementation(projects.core.dataTransfer)
    implementation(projects.core.tutorial)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.google.castFramework)
    implementation(libs.reorderable)
    testImplementation(libs.kotlinx.coroutines.test)
}

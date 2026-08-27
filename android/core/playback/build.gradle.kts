plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.android.hilt)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.playback"
}

dependencies {
    api(projects.core.model)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.google.castFramework)
}

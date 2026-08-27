plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.android.hilt)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.cast_test"
}

dependencies {
    api(projects.core.cast)
    implementation(projects.core.common)

    implementation(libs.kotlinx.coroutines)

    implementation(libs.google.castFramework)
    implementation(libs.hiltTesting)
}

plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.android.hilt)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.data_test"
}

dependencies {
    api(projects.core.data)
    implementation(projects.core.model)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.hiltTesting)
}

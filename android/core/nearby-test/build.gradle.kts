plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.android.hilt)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.nearby_test"
}

dependencies {
    api(projects.core.nearby)
    implementation(projects.core.session)

    implementation(libs.play.services.nearby)
    implementation(libs.hiltTesting)
}

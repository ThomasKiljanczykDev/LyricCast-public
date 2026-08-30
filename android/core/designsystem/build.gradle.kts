plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.compose.library)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.designsystem"
}

dependencies {
    api(projects.core.model)

    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.tooling.preview)
    debugApi(libs.androidx.compose.ui.tooling)
}

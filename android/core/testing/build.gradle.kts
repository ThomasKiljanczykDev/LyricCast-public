plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.android.hilt)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.testing"
}

dependencies {
    implementation(projects.core.common)

    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.androidx.test.coreKtx)
    api(libs.androidx.test.extJunit)
    api(libs.androidx.test.extJunitKtx)
    api(libs.androidx.test.runner)
    api(libs.androidx.rules)
    api(libs.hiltTesting)

    // Compose testing
    api(libs.androidx.compose.ui.test.junit4)
    debugApi(libs.androidx.compose.ui.test.manifest)
}

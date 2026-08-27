plugins {
    alias(libs.plugins.lyriccast.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.feature.session.impl"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.nearby)
    implementation(projects.core.session)

    implementation(libs.androidx.compose.activity)
    implementation(libs.play.services.nearby)
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(projects.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    // The client ViewModel builds a SettingsRepository over a test DataStore.
    testImplementation(libs.androidx.datastore)
}

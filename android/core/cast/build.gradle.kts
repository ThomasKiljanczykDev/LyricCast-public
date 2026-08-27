plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.compose.library)
    alias(libs.plugins.lyriccast.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.core.cast"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.session)
    api(projects.core.model)
    implementation(projects.core.datastoreProto)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.google.castFramework)
    implementation(libs.androidx.mediaRouter)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.foundation.layout)

    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}

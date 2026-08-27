plugins {
    alias(libs.plugins.lyriccast.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.feature.settings.impl"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlinLite)
}

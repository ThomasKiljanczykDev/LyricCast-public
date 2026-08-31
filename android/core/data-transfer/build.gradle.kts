plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
        consumerProguardFiles("consumer-rules.pro")
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.datatransfer"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.common)

    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
}

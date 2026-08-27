plugins {
    alias(libs.plugins.lyriccast.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        minSdk = 27
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.feature.category.impl"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)

    implementation(libs.kotlinx.collections.immutable)
    testImplementation(libs.kotlinx.coroutines.test)
}

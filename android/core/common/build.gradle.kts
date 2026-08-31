plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.android.hilt)
}

android {
    defaultConfig {
        minSdk = 27
        consumerProguardFiles("consumer-rules.pro")
    }

    namespace = "dev.thomas_kiljanczyk.lyriccast.common"
}

dependencies {
    implementation(libs.apache.commonsLang)
    implementation(libs.zip4j)
    implementation(libs.android.material)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
}

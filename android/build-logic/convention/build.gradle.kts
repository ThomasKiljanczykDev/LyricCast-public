/*
 * Convention plugins for LyricCast modules
 */

plugins {
    `kotlin-dsl`
}

group = "dev.thomas_kiljanczyk.lyriccast.buildlogic"

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.serialization.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "lyriccast.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "lyriccast.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "lyriccast.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "lyriccast.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "lyriccast.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("kotlinQuality") {
            id = "lyriccast.kotlin.quality"
            implementationClass = "KotlinQualityConventionPlugin"
        }
        register("composeLibrary") {
            id = "lyriccast.compose.library"
            implementationClass = "ComposeLibraryConventionPlugin"
        }
    }
}

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Convention plugin for feature modules.
 * Combines Android library, Compose, and Hilt configurations.
 * Also adds common feature module dependencies.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("lyriccast.android.library")
                apply("lyriccast.compose.library")
                apply("lyriccast.android.hilt")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                "implementation"(project(":core:designsystem"))
                "implementation"(project(":core:ui"))

                "implementation"(libs.findLibrary("androidx.compose.material3").get())
                "implementation"(libs.findLibrary("androidx.compose.material.iconsExtended").get())
                "implementation"(
                    libs.findLibrary("androidx.compose.material3.adaptive.navigation.suite").get()
                )
                "implementation"(libs.findLibrary("androidx.compose.viewmodel").get())
                "implementation"(libs.findLibrary("androidx.compose.hilt").get())
                "implementation"(libs.findLibrary("androidx.compose.hilt.lifecycle").get())
                "implementation"(libs.findLibrary("androidx.compose.navigation").get())
                "implementation"(libs.findLibrary("kotlinx.collections.immutable").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())

                "testImplementation"(libs.findLibrary("junit").get())
                "testImplementation"(libs.findLibrary("google.truth").get())
            }
        }
    }
}

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("lyriccast.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<ApplicationExtension> {
                buildFeatures {
                    compose = true
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                "implementation"(libs.findLibrary("androidx.compose.ui").get())
                "implementation"(libs.findLibrary("androidx.compose.ui.graphics").get())
                "implementation"(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
                "implementation"(libs.findLibrary("androidx.compose.material3").get())
                "implementation"(libs.findLibrary("androidx.compose.material.iconsExtended").get())
                "implementation"(
                    libs.findLibrary("androidx.compose.material3.adaptive.navigation.suite").get()
                )
                "implementation"(libs.findLibrary("androidx.compose.activity").get())
                "implementation"(libs.findLibrary("androidx.compose.viewmodel").get())
                "implementation"(libs.findLibrary("androidx.compose.navigation").get())
                "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling").get())
                "debugImplementation"(libs.findLibrary("androidx.compose.ui.test.manifest").get())
                "androidTestImplementation"(
                    libs.findLibrary("androidx.compose.ui.test.junit4").get()
                )
            }
        }
    }
}

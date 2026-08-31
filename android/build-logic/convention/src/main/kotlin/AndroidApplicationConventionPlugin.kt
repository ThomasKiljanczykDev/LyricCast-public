import com.android.build.api.dsl.ApplicationExtension
import dev.thomas_kiljanczyk.lyriccast.buildlogic.configureDetekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            configureDetekt()

            extensions.configure<ApplicationExtension> {
                compileSdk = 37

                defaultConfig {
                    minSdk = 29
                    targetSdk = 37
                }

                compileOptions {
                    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17
                    targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
                    isCoreLibraryDesugaringEnabled = true
                }

                buildTypes {
                    release {
                        ndk {
                            debugSymbolLevel = "FULL"
                        }
                    }

                    create("seededRelease") {
                        initWith(getByName("release"))
                        matchingFallbacks.add("release")
                    }
                }

                testOptions {
                    animationsDisabled = true

                    unitTests {
                        isReturnDefaultValues = true
                        isIncludeAndroidResources = true
                    }
                }
            }

            val libs =
                extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
                    .named("libs")
            dependencies {
                add("coreLibraryDesugaring", libs.findLibrary("android-desugarJdkLibs").get())
            }
        }
    }
}

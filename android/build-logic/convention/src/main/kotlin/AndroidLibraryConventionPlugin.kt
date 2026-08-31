import com.android.build.api.dsl.LibraryExtension
import dev.thomas_kiljanczyk.lyriccast.buildlogic.configureDetekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            configureDetekt()

            extensions.configure<LibraryExtension> {
                compileSdk = 37

                defaultConfig {
                    minSdk = 29
                }

                buildTypes {
                    release {
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )

                        ndk {
                            debugSymbolLevel = "FULL"
                        }
                    }

                    create("seededRelease") {
                        initWith(getByName("release"))
                        matchingFallbacks.add("release")
                    }
                }

                compileOptions {
                    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17
                    targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
                    isCoreLibraryDesugaringEnabled = true
                }

                testOptions {
                    unitTests {
                        isIncludeAndroidResources = true
                        isReturnDefaultValues = true
                    }
                }
            }

            tasks.withType<Test> {
                failOnNoDiscoveredTests.set(false)
            }

            // Material 3 dynamic colors need AppCompat resources at test time
            val libs =
                extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
                    .named("libs")
            dependencies {
                add("coreLibraryDesugaring", libs.findLibrary("android-desugarJdkLibs").get())
                add("testImplementation", libs.findLibrary("android-material").get())
                add("testImplementation", libs.findLibrary("androidx-appcompat").get())
                add("androidTestImplementation", libs.findLibrary("android-material").get())
                add("androidTestImplementation", libs.findLibrary("androidx-appcompat").get())
                add("androidTestImplementation", libs.findLibrary("androidx-test-runner").get())
            }
        }
    }
}

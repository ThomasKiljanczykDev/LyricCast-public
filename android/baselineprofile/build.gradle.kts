plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.lyriccast.kotlin.quality)
}

android {
    namespace = "dev.thomas_kiljanczyk.lyriccast.baselineprofile"
    compileSdk = 37

    kotlin {
        jvmToolchain(17)
    }

    defaultConfig {
        minSdk = 29
        targetSdk = 37

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        create("seededRelease") {
        }
    }
    targetProjectPath = ":app"

    // Gradle Managed Device used to generate the baseline profile in CI. "Latest" API (36) on a
    // representative phone; google-atd so the seeded release app's Firebase/Cast init has Play
    // Services available during the generation journeys.
    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            localDevices {
                create("pixel6Api36") {
                    device = "Pixel 6"
                    apiLevel = 36
                    systemImageSource = "google-atd"
                    // Force the x86_64 image. require64Bit defaults to false, so GMD would pick a
                    // 32-bit x86 image where one exists (it can't boot headless on CI). testedAbi
                    // only pins the tested APK's ABI and pre-answers AGP 10.0's default flip to
                    // arm64-v8a.
                    require64Bit = true
                    testedAbi = "x86_64"
                }
            }
        }
    }
}

// This is the configuration block for the Baseline Profile plugin.
// Generation runs on the Gradle Managed Device defined above (not connected devices), so CI can
// generate the profile headlessly and the GMD path is exercised end to end.
baselineProfile {
    managedDevices += "pixel6Api36"
    useConnectedDevices = false
}

dependencies {
    implementation(libs.androidx.test.extJunit)
    implementation(libs.androidx.test.espresso)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}

androidComponents {
    onVariants { v ->
        val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
        @Suppress("UnstableApiUsage")
        v.instrumentationRunnerArguments.put(
            "targetAppId",
            v.testedApks.map { artifactsLoader.load(it)?.applicationId }
        )
    }
}

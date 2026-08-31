plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.navigationSafeArgs)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.googleServices)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.lyriccast.kotlin.quality)
    alias(libs.plugins.baselineprofile)
}

android {
    val major = 2
    val minor = 0
    val patch = 0
    // used for hotfix, alpha, beta, etc. versions
    val revision = 2

    defaultConfig {
        applicationId = "dev.thomas_kiljanczyk.lyriccast"
        minSdk = 27
        targetSdk = 35

        // Versioning
        // Max version code is 2,100,000,000
        // Version code is calculated as follows:
        // revision - up to 99
        // patch - up to 999
        // minor - up to 999
        // major - up to 21
        versionCode = major * 100_000_000 + minor * 100_000 + patch * 100 + revision
        versionName = "$major.$minor.$patch${if (revision > 0) ".$revision" else ""}"

        testInstrumentationRunner = "dev.thomas_kiljanczyk.lyriccast.core.testing.LyricCastTestRunner"
        androidResources {
            localeFilters.addAll(
                listOf(
                    "en", "pl", "am", "de", "es", "fil", "fr", "in", "it", "ko", "pt", "sw", "vi",
                    "b+zh+Hans"
                )
            )
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            ndk {
                debugSymbolLevel = "FULL"
            }
        }

        // Used only to generate/benchmark the baseline profile (see :baselineprofile). Signed
        // with the debug key so CI and local runs don't need a release signing config.
        create("seededRelease") {
            initWith(getByName("release"))
            matchingFallbacks.add("release")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // Wired by name, because the baseline-profile plugin derives benchmark* and nonMinified*
    // build types from both release and seededRelease: four per-build-type source sets to keep
    // in step otherwise. It cannot live in main, which every variant compiles.
    sourceSets.configureEach {
        if (name == "main" || name.startsWith("test") || name.startsWith("androidTest")) {
            return@configureEach
        }
        val defaults = if (name.contains("seeded", ignoreCase = true)) "seeded" else "notSeeded"
        kotlin.directories.add("src/$defaults/java")
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }

        // Gradle Managed Devices for the instrumented-test matrix.
        // Each device launches in its natural orientation, so device choice encodes
        // orientation: phones (Pixel 2) are portrait, large tablets (Pixel Tablet) are
        // landscape.
        //
        // ATD ("aosp-atd") images are headless and only published for API 30+, so the matrix
        // spans API 30 (lowest ATD) and 36 (latest).
        @Suppress("UnstableApiUsage")
        managedDevices {
            localDevices {
                create("pixel2Api30") {
                    device = "Pixel 2"
                    apiLevel = 30
                    systemImageSource = "aosp-atd"
                    // API 30 ATD still ships a 32-bit x86 image, which GMD selects by default
                    // (require64Bit defaults to false). That image can't boot headless on the
                    // CI runner ("Unable to find device serial"), so force the x86_64 image.
                    // testedAbi only pins the tested APK's ABI (no native code here, so it's a
                    // no-op for image selection) and pre-answers AGP 10.0's default flip to
                    // arm64-v8a.
                    require64Bit = true
                    testedAbi = "x86_64"
                }
                create("pixel2Api36") {
                    device = "Pixel 2"
                    apiLevel = 36
                    systemImageSource = "aosp-atd"
                    require64Bit = true
                    testedAbi = "x86_64"
                }
                create("pixelTabletApi30") {
                    device = "Pixel Tablet"
                    apiLevel = 30
                    systemImageSource = "aosp-atd"
                    require64Bit = true
                    testedAbi = "x86_64"
                }
                create("pixelTabletApi36") {
                    device = "Pixel Tablet"
                    apiLevel = 36
                    systemImageSource = "aosp-atd"
                    require64Bit = true
                    testedAbi = "x86_64"
                }
            }

            // Convenience group to run the whole matrix locally with
            // `./gradlew ciMatrixGroupDebugAndroidTest`. CI runs the per-device tasks instead
            // so each cell is an isolated, parallel matrix job.
            val matrixDevices = localDevices
            groups.create("ciMatrix") {
                targetDevices.addAll(
                    listOf(
                        "pixel2Api30",
                        "pixel2Api36",
                        "pixelTabletApi30",
                        "pixelTabletApi36"
                        //noinspection WrongGradleMethod
                    ).map { matrixDevices.getByName(it) }
                )
            }
        }
    }
    namespace = "dev.thomas_kiljanczyk.lyriccast"
    compileSdk = 37
}

baselineProfile {
    variants {
        create("seededRelease") {
            mergeIntoMain = true
        }
    }
}

dependencies {
    baselineProfile(projects.baselineprofile)

    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.dataTransfer)
    implementation(projects.core.datastoreProto)
    implementation(projects.core.database)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.session)
    implementation(projects.core.nearby)
    implementation(projects.core.cast)
    implementation(projects.core.playback)
    implementation(projects.core.sync)
    implementation(projects.core.tutorial)

    implementation(projects.feature.category.impl)
    implementation(projects.feature.main.impl)
    implementation(projects.feature.session.impl)
    implementation(projects.feature.setlist.impl)
    implementation(projects.feature.settings.impl)
    implementation(projects.feature.song.impl)

    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.play.services.nearby)

    implementation(libs.androidx.datastore)

    implementation(libs.androidx.profileinstaller)

    implementation(libs.android.material)
    implementation(libs.androidx.coreKtx)

    implementation(libs.google.castFramework)
    implementation(libs.androidx.mediaRouter)

    implementation(libs.hilt)
    ksp(libs.hiltCompiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlyticsKtx)
    implementation(libs.firebase.analyticsKtx)

    testImplementation(libs.junit)
    testImplementation(libs.google.truth)

    androidTestImplementation(libs.androidx.test.coreKtx)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.androidx.test.extJunitKtx)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.androidx.test.espressoContrib) {
        // TODO: nice to have - workaround for protobuf-lite test issues, try to remove it in the future
        // Source: https://stackoverflow.com/questions/66154727/java-lang-nosuchmethoderror-no-static-method-registerdefaultinstance-with-fireb
        exclude(module = "protobuf-lite")
    }
    androidTestImplementation(libs.androidx.rules)

    androidTestImplementation(libs.hiltTesting)
    kspAndroidTest(libs.hiltCompiler)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.dataTest)
    androidTestImplementation(projects.core.nearbyTest)
    androidTestImplementation(projects.core.castTest)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.viewmodel)
    implementation(libs.androidx.compose.hilt)
    implementation(libs.androidx.compose.hilt.lifecycle)
    implementation(libs.androidx.compose.navigation)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    implementation(libs.reorderable)

    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.apache.commonsLang)
    implementation(libs.zip4j)

    coreLibraryDesugaring(libs.android.desugarJdkLibs)
}

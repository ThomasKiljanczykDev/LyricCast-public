plugins {
    alias(libs.plugins.lyriccast.android.library)
    alias(libs.plugins.lyriccast.compose.library)
    alias(libs.plugins.android.compose.screenshot)
}

android {
    namespace = "dev.thomas_kiljanczyk.lyriccast.tools.gplayscreenshots"
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

tasks.withType<Test>().configureEach {
    maxHeapSize = "6g"
}

// `src/main` holds no code -- every composable lives in `src/screenshotTest`, so this module
// contributes nothing to any shipped APK.
//
// Each dependency is declared TWICE, on `implementation` and on `screenshotTestImplementation`,
// because the two halves of the screenshot pipeline read different classpaths:
//   * the plugin's preview scanner walks the *main* runtime classpath for `@Preview`; with the
//     deps only on `screenshotTestImplementation` it finds zero previews.
//   * JUnit's ClassSelectorResolver reflectively loads each preview class and calls
//     `getMethods()`, which needs Compose on the *screenshotTest* runtime classpath --
//     `implementation` does not propagate there.
// Either half failing is silent: discovery issues are swallowed and the build goes green with
// zero rendered images.
fun DependencyHandlerScope.screenshotDependency(dependency: Any) {
    implementation(dependency)
    screenshotTestImplementation(dependency)
}

dependencies {
    screenshotDependency(projects.core.ui)
    screenshotDependency(projects.core.designsystem)
    screenshotDependency(projects.core.model)
    screenshotDependency(projects.core.common)
    screenshotDependency(projects.feature.main.impl)
    screenshotDependency(projects.feature.setlist.impl)
    screenshotDependency(projects.feature.session.impl)
    screenshotDependency(projects.feature.settings.impl)
    screenshotDependency(projects.core.playback)
    screenshotDependency(projects.core.dataTransfer)

    screenshotDependency(libs.androidx.compose.ui)
    screenshotDependency(libs.androidx.compose.ui.tooling.preview)
    screenshotDependency(libs.androidx.compose.material3)
    screenshotDependency(libs.androidx.compose.material.iconsExtended)
    screenshotDependency(libs.kotlinx.collections.immutable)

    // Supplies @PreviewTest, which the screenshot plugin requires on every rendered preview.
    screenshotTestImplementation(libs.android.screenshot.validation.api)
}

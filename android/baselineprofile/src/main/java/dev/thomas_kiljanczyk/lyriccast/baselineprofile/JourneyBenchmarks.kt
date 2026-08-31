package dev.thomas_kiljanczyk.lyriccast.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmarks specific user journeys to validate baseline profile effectiveness
 * beyond startup performance.
 *
 * ## Journeys Tested:
 * - **Scroll Performance**: Song/Setlist list scrolling smoothness
 * - **Navigation Performance**: Tab switching between Songs/Setlists
 *
 * ## Running Benchmarks:
 * ```
 * ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest \
 *     -Pandroid.testInstrumentationRunnerArguments.class=\
 *     dev.thomas_kiljanczyk.lyriccast.baselineprofile.JourneyBenchmarks
 * ```
 *
 * Run on a physical device for accurate results.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class JourneyBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    private val packageName: String
        get() = InstrumentationRegistry.getArguments().getString("targetAppId")
            ?: throw Exception("targetAppId not passed as instrumentation runner arg")

    @Test
    fun scrollPerformanceNone() =
        benchmarkScroll(CompilationMode.None())

    @Test
    fun scrollPerformanceBaselineProfiles() =
        benchmarkScroll(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun benchmarkScroll(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = packageName,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            iterations = 10,
            setupBlock = {
                pressHome()
                startActivityAndWait()
                waitForContent()
            },
            measureBlock = {
                scrollMainList()
            }
        )
    }

    @Test
    fun navigationPerformanceNone() =
        benchmarkNavigation(CompilationMode.None())

    @Test
    fun navigationPerformanceBaselineProfiles() =
        benchmarkNavigation(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun benchmarkNavigation(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = packageName,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            iterations = 10,
            setupBlock = {
                pressHome()
                startActivityAndWait()
                waitForContent()
            },
            measureBlock = {
                navigateBetweenTabs()
            }
        )
    }
}

private fun MacrobenchmarkScope.waitForContent() {
    device.waitForIdle()
    Thread.sleep(500)
}

private fun MacrobenchmarkScope.scrollMainList() {
    val scrollable = device.findObject(By.scrollable(true))
    if (scrollable != null && scrollable.isScrollable) {
        scrollable.scroll(Direction.DOWN, 1.0f)
        device.waitForIdle()
        scrollable.scroll(Direction.UP, 1.0f)
        device.waitForIdle()
        scrollable.scroll(Direction.DOWN, 0.5f)
        device.waitForIdle()
    }
}

private fun MacrobenchmarkScope.navigateBetweenTabs() {
    device.findObject(By.text("Setlists"))?.click()
    device.waitForIdle()
    Thread.sleep(300)

    device.findObject(By.text("Songs"))?.click()
    device.waitForIdle()
    Thread.sleep(300)

    device.findObject(By.text("Setlists"))?.click()
    device.waitForIdle()
}

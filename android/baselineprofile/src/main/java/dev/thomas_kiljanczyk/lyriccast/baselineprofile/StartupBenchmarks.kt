/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 AM
 */

package dev.thomas_kiljanczyk.lyriccast.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmarks app startup performance across different compilation modes.
 *
 * ## Compilation Modes Tested:
 * - **None**: No pre-compilation (worst case, JIT only)
 * - **Partial (Baseline Profiles)**: Uses baseline profiles for targeted optimization
 * - **Full**: Complete AOT compilation (best possible, but larger APK)
 *
 * ## Metrics Captured:
 * - **StartupTimingMetric**: Time to initial display and fully drawn
 * - **FrameTimingMetric**: Frame timing during startup for jank detection
 *
 * ## Running Benchmarks:
 * ```
 * ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest
 * ```
 *
 * Run on a physical device for accurate results. The app uses ReportDrawnWhen
 * to signal when it's fully drawn (after settings are loaded).
 *
 * For more information, see the [Macrobenchmark documentation](https://d.android.com/macrobenchmark#create-macrobenchmark)
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {
    @get:Rule
    val rule = MacrobenchmarkRule()

    private val packageName: String
        get() = InstrumentationRegistry.getArguments().getString("targetAppId")
            ?: throw Exception("targetAppId not passed as instrumentation runner arg")

    /**
     * Baseline: No compilation, JIT only.
     * Represents worst-case performance for first-time users.
     */
    @Test
    fun startupCompilationNone() =
        benchmarkStartup(CompilationMode.None())

    /**
     * With Baseline Profiles: Partial AOT compilation.
     * Represents typical performance after profile installation.
     */
    @Test
    fun startupCompilationBaselineProfiles() =
        benchmarkStartup(CompilationMode.Partial(BaselineProfileMode.Require))

    /**
     * Full AOT compilation: Best possible performance.
     * Useful as an upper bound comparison.
     */
    @Test
    fun startupCompilationFull() =
        benchmarkStartup(CompilationMode.Full())

    private fun benchmarkStartup(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = packageName,
            metrics = listOf(
                StartupTimingMetric(),
                FrameTimingMetric()
            ),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 15,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()
            }
        )
    }
}

package dev.thomas_kiljanczyk.lyriccast.baselineprofile

import android.net.Uri
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a comprehensive baseline profile for the target package,
 * covering startup and critical user journeys.
 *
 * ## User Journeys Covered:
 * - App startup and initial content loading
 * - Songs tab navigation and list scrolling
 * - Setlists tab navigation and list scrolling
 * - Settings screen access
 * - Song creation flow (partial)
 *
 * ## Running the Generator:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 *
 * After running, verify improvements with [StartupBenchmarks] and [JourneyBenchmarks].
 *
 * Requirements: API 33+ or rooted API 28+, androidx.benchmark 1.2.0+
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    private val packageName: String
        get() = InstrumentationRegistry.getArguments().getString("targetAppId")
            ?: throw Exception("targetAppId not passed as instrumentation runner arg")

    private fun triggerSeeding() {
        val uri = Uri.parse("content://$packageName.baselineprofileseeding")
        InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
            .call(uri, "seed", null, null)
    }

    @Test
    fun generate() {
        triggerSeeding()

        rule.collect(
            packageName = packageName,
            includeInStartupProfile = true
        ) {
            pressHome()
            startActivityAndWait()
            waitForContent()

            scrollListIfPresent()

            navigateToTab("Setlists")
            waitForContent()
            scrollListIfPresent()

            navigateToTab("Songs")
            waitForContent()

            openSettings()
            waitForContent()
            scrollSettingsIfPresent()
            navigateBack()

            openFabMenu()
            dismissFabMenu()

            // Unlike the private client, this app does not have a "Show tutorial again"
            // settings entry point, so there is no deterministic way to re-enter the onboarding
            // tutorial from a fresh (seeded) install here -- that journey is dropped rather than
            // adapted.
        }
    }
}

private const val SHORT_TIMEOUT_MS = 2_000L

private fun MacrobenchmarkScope.waitForContent() {
    device.waitForIdle()
    Thread.sleep(500)
}

private fun MacrobenchmarkScope.navigateToTab(tabName: String) {
    val tab = device.findObject(By.text(tabName))
    tab?.click()
    device.waitForIdle()
}

private fun MacrobenchmarkScope.scrollListIfPresent() {
    val scrollable = device.findObject(By.scrollable(true))
    if (scrollable != null && scrollable.isScrollable) {
        scrollable.scroll(Direction.DOWN, 0.8f)
        device.waitForIdle()
        scrollable.scroll(Direction.UP, 0.8f)
        device.waitForIdle()
    }
}

private fun MacrobenchmarkScope.openSettings() {
    val moreOptions = device.findObject(By.desc("More options"))
    if (moreOptions != null) {
        moreOptions.click()
        device.waitForIdle()

        val settingsItem = device.wait(Until.findObject(By.text("Settings")), SHORT_TIMEOUT_MS)
        settingsItem?.click()
        device.waitForIdle()
    }
}

private fun MacrobenchmarkScope.scrollSettingsIfPresent() {
    val scrollable = device.findObject(By.scrollable(true))
    if (scrollable != null && scrollable.isScrollable) {
        scrollable.scroll(Direction.DOWN, 0.5f)
        device.waitForIdle()
    }
}

private fun MacrobenchmarkScope.navigateBack() {
    device.pressBack()
    device.waitForIdle()
}

private fun MacrobenchmarkScope.openFabMenu() {
    val fab = device.findObject(By.desc("Add song"))
    fab?.click()
    device.waitForIdle()
}

private fun MacrobenchmarkScope.dismissFabMenu() {
    device.pressBack()
    device.waitForIdle()
}

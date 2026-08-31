package dev.thomas_kiljanczyk.lyriccast.tests.ui.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.R
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.REPLAY_REQUESTED_VERSION
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.onboardingSlides
import dev.thomas_kiljanczyk.lyriccast.core.ui.testing.TestTags
import dev.thomas_kiljanczyk.lyriccast.modules.TestAppModule
import dev.thomas_kiljanczyk.lyriccast.ui.main.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The guided tour is deliberately not driven here:
 * its sequencing and abort rules are unit tested,
 * and a fifteen-step walk across four screens is the kind of device test that turns flaky.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class OnboardingCarouselComposeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createEmptyComposeRule()

    private var scenario: ActivityScenario<MainActivity>? = null

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun requestOnboardingAndLaunch() {
        TestAppModule.cleanupDataStore()
        TestAppModule.initialOnboardingVersion = REPLAY_REQUESTED_VERSION

        hiltRule.inject()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun closeActivity() {
        scenario?.close()
        scenario = null
    }

    @Test
    fun carouselIsShownAndPagesForward() {
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_slide_welcome_title))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(TestTags.ONBOARDING_NEXT_BUTTON).performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_slide_library_title))
            .assertIsDisplayed()
    }

    @Test
    fun systemBackReturnsToThePreviousSlide() {
        composeTestRule.onNodeWithTag(TestTags.ONBOARDING_NEXT_BUTTON).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_slide_library_title))
            .assertIsDisplayed()

        Espresso.pressBack()

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_slide_welcome_title))
            .assertIsDisplayed()
    }

    @Test
    fun backButtonIsDisabledOnTheFirstSlideOnly() {
        composeTestRule.onNodeWithTag(TestTags.ONBOARDING_PREVIOUS_BUTTON).assertIsNotEnabled()

        composeTestRule.onNodeWithTag(TestTags.ONBOARDING_NEXT_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.ONBOARDING_PREVIOUS_BUTTON).assertIsEnabled()

        composeTestRule.onNodeWithTag(TestTags.ONBOARDING_PREVIOUS_BUTTON).performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_slide_welcome_title))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.ONBOARDING_PREVIOUS_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun lastSlideOffersToStart() {
        repeat(onboardingSlides.size - 1) {
            composeTestRule.onNodeWithTag(TestTags.ONBOARDING_NEXT_BUTTON).performClick()
        }

        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_get_started))
            .assertIsDisplayed()
    }

    @Test
    fun skipLeavesTheCarousel() {
        composeTestRule.onNodeWithTag(TestTags.ONBOARDING_SKIP_BUTTON).performClick()

        composeTestRule.onNodeWithTag(TestTags.ONBOARDING_PAGER).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.MAIN_MENU_BUTTON).assertIsDisplayed()
    }
}

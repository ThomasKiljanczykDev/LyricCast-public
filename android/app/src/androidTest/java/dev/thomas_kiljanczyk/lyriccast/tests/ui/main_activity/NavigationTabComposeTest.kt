package dev.thomas_kiljanczyk.lyriccast.tests.ui.main_activity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.thomas_kiljanczyk.lyriccast.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigationTabComposeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigationTabsAreWorking() {
        hiltRule.inject()

        composeTestRule.onNodeWithText("Song title").assertIsDisplayed()

        composeTestRule.onNodeWithText("Setlists").performClick()

        composeTestRule.onNodeWithText("Setlist name").assertIsDisplayed()

        composeTestRule.onNodeWithText("Songs").performClick()

        composeTestRule.onNodeWithText("Song title").assertIsDisplayed()
    }
}
/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:15 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 9:58 PM
 */

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

        // Initially on Songs tab - verify song title filter is displayed
        composeTestRule.onNodeWithText("Song title").assertIsDisplayed()

        // Navigate to Setlists tab
        composeTestRule.onNodeWithText("Setlists").performClick()

        // Verify setlist name filter is displayed
        composeTestRule.onNodeWithText("Setlist name").assertIsDisplayed()

        // Navigate back to Songs tab
        composeTestRule.onNodeWithText("Songs").performClick()

        // Verify song title filter is displayed again
        composeTestRule.onNodeWithText("Song title").assertIsDisplayed()
    }
}
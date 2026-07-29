/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 8:53 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 8:10 PM
 */

package dev.thomas_kiljanczyk.lyriccast.shared

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput

/**
 * Utility functions for Compose UI testing
 */
object ComposeTestUtils {

    /**
     * Waits for a node matching the given matcher to appear
     * @param matcher The semantic matcher to find the node
     * @param timeoutMillis Maximum time to wait in milliseconds
     * @return The SemanticsNodeInteraction for the found node
     */
    @OptIn(ExperimentalTestApi::class)
    fun ComposeContentTestRule.waitForNode(
        matcher: SemanticsMatcher,
        timeoutMillis: Long = 5000
    ): SemanticsNodeInteraction {
        this.waitUntilAtLeastOneExists(matcher, timeoutMillis)
        return this.onNode(matcher)
    }

    /**
     * Waits for a node with the given text to appear
     */
    fun ComposeContentTestRule.waitForText(
        text: String,
        timeoutMillis: Long = 5000
    ): SemanticsNodeInteraction {
        return waitForNode(hasText(text), timeoutMillis)
    }

    /**
     * Waits for a node with the given test tag to appear
     */
    fun ComposeContentTestRule.waitForTag(
        tag: String,
        timeoutMillis: Long = 5000
    ): SemanticsNodeInteraction {
        return waitForNode(hasTestTag(tag), timeoutMillis)
    }

    /**
     * Performs a long click on a node with the given text
     */
    fun ComposeContentTestRule.longClickOnText(text: String) {
        onNodeWithText(text).performTouchInput { longClick() }
    }

    /**
     * Clicks on a node with the given content description
     */
    fun ComposeContentTestRule.clickOnContentDescription(description: String) {
        onNodeWithContentDescription(description).performClick()
    }

    /**
     * Enters text in a field with the given label
     */
    fun ComposeContentTestRule.enterTextInField(label: String, text: String) {
        onNodeWithText(label).performTextInput(text)
    }

    /**
     * Clicks on a button with the given text
     */
    fun ComposeContentTestRule.clickButton(text: String) {
        onNodeWithText(text).performClick()
    }

    /**
     * Verifies that a text is displayed
     */
    fun ComposeContentTestRule.assertTextDisplayed(text: String) {
        onNodeWithText(text).assertExists()
    }

    /**
     * Verifies that a text is not displayed
     */
    fun ComposeContentTestRule.assertTextNotDisplayed(text: String) {
        onNodeWithText(text).assertDoesNotExist()
    }

    /**
     * Waits for a condition to be true
     */
    fun ComposeContentTestRule.waitForCondition(
        timeoutMillis: Long = 5000,
        condition: () -> Boolean
    ) {
        waitUntil(timeoutMillis, condition)
    }
}
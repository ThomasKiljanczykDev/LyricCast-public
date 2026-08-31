package dev.thomas_kiljanczyk.lyriccast.core.testing.util

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

object ComposeTestUtils {

    @OptIn(ExperimentalTestApi::class)
    fun ComposeContentTestRule.waitForNode(
        matcher: SemanticsMatcher,
        timeoutMillis: Long = 5000
    ): SemanticsNodeInteraction {
        this.waitUntilAtLeastOneExists(matcher, timeoutMillis)
        return this.onNode(matcher)
    }

    fun ComposeContentTestRule.waitForText(
        text: String,
        timeoutMillis: Long = 5000
    ): SemanticsNodeInteraction {
        return waitForNode(hasText(text), timeoutMillis)
    }

    fun ComposeContentTestRule.waitForTag(
        tag: String,
        timeoutMillis: Long = 5000
    ): SemanticsNodeInteraction {
        return waitForNode(hasTestTag(tag), timeoutMillis)
    }

    fun ComposeContentTestRule.longClickOnText(text: String) {
        onNodeWithText(text).performTouchInput { longClick() }
    }

    fun ComposeContentTestRule.clickOnContentDescription(description: String) {
        onNodeWithContentDescription(description).performClick()
    }

    fun ComposeContentTestRule.enterTextInField(label: String, text: String) {
        onNodeWithText(label).performTextInput(text)
    }

    fun ComposeContentTestRule.clickButton(text: String) {
        onNodeWithText(text).performClick()
    }

    fun ComposeContentTestRule.assertTextDisplayed(text: String) {
        onNodeWithText(text).assertExists()
    }

    fun ComposeContentTestRule.assertTextNotDisplayed(text: String) {
        onNodeWithText(text).assertDoesNotExist()
    }

    fun ComposeContentTestRule.waitForCondition(
        timeoutMillis: Long = 5000,
        condition: () -> Boolean
    ) {
        waitUntil(timeoutMillis, condition)
    }

    fun ComposeContentTestRule.waitUntilAsserted(
        timeoutMillis: Long = 3000,
        assertion: ComposeContentTestRule.() -> Unit
    ) {
        waitUntil(timeoutMillis) {
            try {
                assertion()
                true
            } catch (_: AssertionError) {
                false
            }
        }
    }
}

/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:15 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 10:16 PM
 */

package dev.thomas_kiljanczyk.lyriccast.tests.ui.main_activity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.ui.main.MainActivity
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class DeleteSetlistComposeTest {

    private companion object {
        val setlist1 = Setlist(UUIDv7.randomUUID(), "DeleteSetlistTest 1", listOf())
        val setlist2 = Setlist(UUIDv7.randomUUID(), "DeleteSetlistTest 2", listOf())
        val setlist3 = Setlist(UUIDv7.randomUUID(), "DeleteSetlistTest 3", listOf())
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var setlistsRepository: SetlistsRepository

    @Before
    fun setup() = runTest {
        hiltRule.inject()

        // Add test data
        setlistsRepository.upsertSetlist(setlist1)
        setlistsRepository.upsertSetlist(setlist2)
        setlistsRepository.upsertSetlist(setlist3)

        // Navigate to Setlists tab
        composeTestRule.onNodeWithText("Setlists").performClick()
    }

    @Test
    fun setlistIsDeleted() {
        // Wait for all setlists to appear
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText(setlist1.name)).fetchSemanticsNodes().isNotEmpty()
        }

        // Verify all setlists are displayed initially
        composeTestRule.onNodeWithText(setlist1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertIsDisplayed()

        // Long click on setlist2 to select it
        composeTestRule.onNodeWithText(setlist2.name).performTouchInput { longClick() }

        // Click delete button
        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        // Wait for setlist to be deleted
        composeTestRule.waitUntil(3000) {
            try {
                composeTestRule.onNodeWithText(setlist2.name).assertIsNotDisplayed()
                true
            } catch (_: AssertionError) {
                false
            }
        }

        // Verify setlist2 was deleted and others remain
        composeTestRule.onNodeWithText(setlist1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertIsDisplayed()
    }

    @Test
    fun multipleSetlistsAreDeleted() {
        // Wait for all setlists to appear
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText(setlist1.name)).fetchSemanticsNodes().isNotEmpty()
        }

        // Verify all setlists are displayed initially
        composeTestRule.onNodeWithText(setlist1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertIsDisplayed()

        // Long click on setlist1 to enter selection mode
        composeTestRule.onNodeWithText(setlist1.name).performTouchInput { longClick() }

        // Click on setlist2 to add it to selection
        composeTestRule.onNodeWithText(setlist2.name).performClick()

        // Click delete button
        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        // Wait for setlists to be deleted
        composeTestRule.waitUntil(3000) {
            try {
                composeTestRule.onNodeWithText(setlist1.name).assertIsNotDisplayed()
                composeTestRule.onNodeWithText(setlist2.name).assertIsNotDisplayed()
                true
            } catch (_: AssertionError) {
                false
            }
        }

        // Verify setlist1 and setlist2 were deleted, setlist3 remains
        composeTestRule.onNodeWithText(setlist1.name).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertIsDisplayed()
    }
}
/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 8:53 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 8:09 PM
 */

package dev.thomas_kiljanczyk.lyriccast.tests.ui.main_activity

import androidx.compose.ui.test.assertIsDisplayed
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
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
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
class DeleteSongComposeTest {

    private companion object {
        const val SONG_TITLE = "FilterSongsTest 1"
        val song1 = Song(title = "$SONG_TITLE 1", lyrics = listOf(), presentation = listOf())
        val song2 = Song(title = "$SONG_TITLE 2", lyrics = listOf(), presentation = listOf())
        val song3 = Song(title = "FilterSongsTest 2", lyrics = listOf(), presentation = listOf())
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var songsRepository: SongsRepository

    @Before
    fun setup() = runTest {
        hiltRule.inject()

        // Add test songs to repository
        songsRepository.upsertSong(song1)
        songsRepository.upsertSong(song2)
        songsRepository.upsertSong(song3)
    }

    @Test
    fun songIsDeleted() {
        // Wait for songs to appear
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodes(hasText(song1.title))
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify all songs are displayed
        composeTestRule.onNodeWithText(song1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song2.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song3.title).assertIsDisplayed()

        // Long click on song2 to enter selection mode
        composeTestRule
            .onNodeWithText(song2.title)
            .performTouchInput { longClick() }

        // Click delete action
        composeTestRule
            .onNodeWithContentDescription("Delete")
            .performClick()

        // Verify song2 is deleted, others remain
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule
                    .onNodeWithText(song2.title)
                    .assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithText(song1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song3.title).assertIsDisplayed()
    }

    @Test
    fun multipleSongsAreDeleted() {
        // Wait for songs to appear
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodes(hasText(song1.title))
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify all songs are displayed
        composeTestRule.onNodeWithText(song1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song2.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song3.title).assertIsDisplayed()

        // Long click on song1 to enter selection mode
        composeTestRule
            .onNodeWithText(song1.title)
            .performTouchInput { longClick() }

        // Click on song2 to add to selection
        composeTestRule
            .onNodeWithText(song2.title)
            .performClick()

        // Click delete action
        composeTestRule
            .onNodeWithContentDescription("Delete")
            .performClick()

        // Verify song1 and song2 are deleted, song3 remains
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule
                    .onNodeWithText(song1.title)
                    .assertDoesNotExist()
                composeTestRule
                    .onNodeWithText(song2.title)
                    .assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithText(song3.title).assertIsDisplayed()
    }
}
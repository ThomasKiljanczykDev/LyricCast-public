/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:20 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 12:16 AM
 */

package dev.thomas_kiljanczyk.lyriccast.tests.integration.main_activity

import android.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.testing.util.ComposeTestUtils.waitUntilAsserted
import dev.thomas_kiljanczyk.lyriccast.core.ui.testing.TestTags
import dev.thomas_kiljanczyk.lyriccast.ui.main.MainActivity
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@SmallTest
class FilterSongsComposeTest {

    private companion object {
        val category = Category("TEST CATEGORY", Color.RED)

        const val SONG_TITLE = "FilterSongsTest 1"
        val song1 = Song(
            title = "$SONG_TITLE 1",
            lyrics = listOf(),
            presentation = listOf(),
            category = category
        )
        val song2 = Song(title = "$SONG_TITLE 2", lyrics = listOf(), presentation = listOf())
        val song3 = Song(title = "FilterSongsTest 2", lyrics = listOf(), presentation = listOf())
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var songsRepository: SongsRepository

    @Inject
    lateinit var categoriesRepository: CategoriesRepository

    @Before
    fun setup() = runTest {
        hiltRule.inject()

        // Add test data
        categoriesRepository.upsertCategory(category)
        songsRepository.upsertSong(song1)
        songsRepository.upsertSong(song2)
        songsRepository.upsertSong(song3)
    }

    @Test
    fun songsAreFilteredByTitle() {
        composeTestRule.waitUntilAsserted(5000) {
            onNodeWithText(song1.title).assertExists()
        }

        // Verify all songs are displayed initially
        composeTestRule.onNodeWithText(song1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song2.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song3.title).assertIsDisplayed()

        // Filter by title
        composeTestRule.onNodeWithTag(TestTags.SONG_TITLE_FILTER).performTextInput(SONG_TITLE)

        // Wait for filter to apply
        composeTestRule.waitUntilAsserted(3000) {
            onNodeWithText(song3.title).assertDoesNotExist()
        }

        // Verify filtered results
        composeTestRule.onNodeWithText(song1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song2.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song3.title).assertDoesNotExist()
    }

    @Test
    fun songsAreFilteredByCategory() {
        composeTestRule.waitUntilAsserted(5000) {
            onNodeWithText(song1.title).assertExists()
        }

        // Verify all songs are displayed initially
        composeTestRule.onNodeWithText(song1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song2.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song3.title).assertIsDisplayed()

        // Click on category dropdown
        composeTestRule.onNodeWithTag(TestTags.CATEGORY_DROPDOWN).performClick()

        composeTestRule.onNode(hasText(category.name) and hasAnyAncestor(isPopup())).performClick()

        // Wait for filter to apply
        composeTestRule.waitUntilAsserted(3000) {
            onNodeWithText(song2.title).assertDoesNotExist()
            onNodeWithText(song3.title).assertDoesNotExist()
        }

        // Verify only song with category is shown
        composeTestRule.onNodeWithText(song1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song2.title).assertDoesNotExist()
        composeTestRule.onNodeWithText(song3.title).assertDoesNotExist()
    }
}
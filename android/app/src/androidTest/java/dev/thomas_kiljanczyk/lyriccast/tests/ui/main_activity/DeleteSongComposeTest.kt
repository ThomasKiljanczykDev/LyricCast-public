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

        songsRepository.upsertSong(song1)
        songsRepository.upsertSong(song2)
        songsRepository.upsertSong(song3)
    }

    @Test
    fun songIsDeleted() {
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodes(hasText(song1.title))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(song1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song2.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song3.title).assertIsDisplayed()

        composeTestRule
            .onNodeWithText(song2.title)
            .performTouchInput { longClick() }

        composeTestRule
            .onNodeWithContentDescription("Delete")
            .performClick()

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
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodes(hasText(song1.title))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(song1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song2.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(song3.title).assertIsDisplayed()

        composeTestRule
            .onNodeWithText(song1.title)
            .performTouchInput { longClick() }

        composeTestRule
            .onNodeWithText(song2.title)
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Delete")
            .performClick()

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
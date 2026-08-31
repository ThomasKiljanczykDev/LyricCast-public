package dev.thomas_kiljanczyk.lyriccast.tests.integration.main_activity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
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
@SmallTest
class FilterSetlistsComposeTest {

    private companion object {
        const val SETLIST_NAME = "FilterSetlistsTest 1"
        val setlist1 = Setlist(UUIDv7.randomUUID(), "$SETLIST_NAME 1", listOf())
        val setlist2 = Setlist(UUIDv7.randomUUID(), "$SETLIST_NAME 2", listOf())
        val setlist3 = Setlist(UUIDv7.randomUUID(), "FilterSetlistsTest 2", listOf())
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

        setlistsRepository.upsertSetlist(setlist1)
        setlistsRepository.upsertSetlist(setlist2)
        setlistsRepository.upsertSetlist(setlist3)

        composeTestRule.onNodeWithText("Setlists").performClick()
    }

    @Test
    fun setlistsAreFilteredByName() {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText(setlist1.name)).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(setlist1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertIsDisplayed()

        composeTestRule.onNodeWithText("Setlist name").performTextInput(SETLIST_NAME)

        composeTestRule.waitUntil(3000) {
            try {
                composeTestRule.onNodeWithText(setlist3.name).assertDoesNotExist()
                true
            } catch (_: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithText(setlist1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertDoesNotExist()
    }
}
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

        setlistsRepository.upsertSetlist(setlist1)
        setlistsRepository.upsertSetlist(setlist2)
        setlistsRepository.upsertSetlist(setlist3)

        composeTestRule.onNodeWithText("Setlists").performClick()
    }

    @Test
    fun setlistIsDeleted() {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText(setlist1.name)).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(setlist1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertIsDisplayed()

        composeTestRule.onNodeWithText(setlist2.name).performTouchInput { longClick() }

        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        composeTestRule.waitUntil(3000) {
            try {
                composeTestRule.onNodeWithText(setlist2.name).assertIsNotDisplayed()
                true
            } catch (_: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithText(setlist1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertIsDisplayed()
    }

    @Test
    fun multipleSetlistsAreDeleted() {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText(setlist1.name)).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(setlist1.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertIsDisplayed()

        composeTestRule.onNodeWithText(setlist1.name).performTouchInput { longClick() }

        composeTestRule.onNodeWithText(setlist2.name).performClick()

        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        composeTestRule.waitUntil(3000) {
            try {
                composeTestRule.onNodeWithText(setlist1.name).assertIsNotDisplayed()
                composeTestRule.onNodeWithText(setlist2.name).assertIsNotDisplayed()
                true
            } catch (_: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithText(setlist1.name).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(setlist2.name).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(setlist3.name).assertIsDisplayed()
    }
}
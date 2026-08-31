package dev.thomas_kiljanczyk.lyriccast.tests.ui.category_manager

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
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.ui.testing.TestTags
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui.colorItems
import dev.thomas_kiljanczyk.lyriccast.ui.main.MainActivity
import javax.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class AddCategoryComposeTest {

    private companion object {
        const val NEW_CATEGORY_NAME = "AddCategoryTest 2"
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var categoriesRepository: CategoriesRepository

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.onNodeWithTag(TestTags.MAIN_MENU_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.MAIN_MENU_MANAGE_CATEGORIES).performClick()
    }

    @Test
    fun categoryIsAdded() = runTest {
        val colorName = colorItems[0].name.toString(
            InstrumentationRegistry.getInstrumentation().targetContext
        )

        composeTestRule
            .onNodeWithTag(TestTags.ADD_CATEGORY_BUTTON)
            .performClick()

        composeTestRule
            .onNodeWithTag(TestTags.ADD_EDIT_CATEGORY_DIALOG)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(TestTags.CATEGORY_NAME_FIELD)
            .performTextInput(NEW_CATEGORY_NAME)

        composeTestRule
            .onNodeWithTag(TestTags.CATEGORY_COLOR_DROPDOWN)
            .performClick()

        composeTestRule.onNode(hasText(colorName) and hasAnyAncestor(isPopup())).performClick()

        composeTestRule
            .onNodeWithTag(TestTags.CATEGORY_SAVE_BUTTON)
            .performClick()

        composeTestRule
            .onNodeWithText(NEW_CATEGORY_NAME.uppercase())
            .assertIsDisplayed()
    }
}

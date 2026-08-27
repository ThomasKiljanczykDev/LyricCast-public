/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:15 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 12:15 AM
 */

package dev.thomas_kiljanczyk.lyriccast.tests.ui.category_manager

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.ui.category_manager.CategoryManagerScreen
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.colorItems
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

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
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var categoriesRepository: CategoriesRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    @Ignore("TODO: test configuration requires tweaks to allow injection of hilt view models in custom test rule content")
    fun categoryIsAdded() = runTest {
        // Get the color name from resources
        val colorName = colorItems[0].name.toString(
            InstrumentationRegistry.getInstrumentation().targetContext
        )

        // Setup the screen
        composeTestRule.setContent {
            LyricCastTheme {
                CategoryManagerScreen(
                    onNavigateUp = { }
                )
            }
        }

        // Click on Add Category button (FAB or menu item)
        composeTestRule
            .onNodeWithContentDescription("Add category")
            .performClick()

        // Verify dialog is shown
        composeTestRule
            .onNodeWithText("Add category")
            .assertIsDisplayed()

        // Enter category name
        composeTestRule
            .onNodeWithText("Name")
            .performTextInput(NEW_CATEGORY_NAME)

        // Click on color dropdown
        composeTestRule
            .onNodeWithText("Color")
            .performClick()

        // Select a color from dropdown
        composeTestRule
            .onNodeWithText(colorName)
            .performClick()

        // Click Save/Add button
        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // Verify the category appears in the list
        composeTestRule
            .onNodeWithText(NEW_CATEGORY_NAME.uppercase())
            .assertIsDisplayed()
    }
}
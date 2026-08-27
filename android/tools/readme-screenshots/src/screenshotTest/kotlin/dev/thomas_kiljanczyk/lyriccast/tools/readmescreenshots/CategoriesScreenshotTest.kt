/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.ScreenshotData
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.rememberScreenshotData
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui.AddOrEditCategoryDialog
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui.CategoryManagerScreen
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui.MutableAddOrEditCategoryState
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui.MutableCategoryManagerState
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui.colorItems
import kotlinx.collections.immutable.toPersistentList

/**
 * The name typed into the add dialog. Deliberately not one of the three already in the list behind
 * it -- the shot is of a category being *added*, and a duplicate name would be a validation error.
 */
private const val NEW_CATEGORY_NAME = "EVEN BETTER STUFF"

/** Not `colorItems.first()`, which is what an untouched form would already show. */
private const val NEW_CATEGORY_COLOR_INDEX = 2

/** `LyricCast-categories-1.png` and `LyricCast-categories-2.png`. */
class CategoriesScreenshotTest {
    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun CategoryList() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                CategoryManagerScreenshot(data)
            }
        }
    }

    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun CategoryEditor() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                DialogScreenshot(
                    background = { CategoryManagerScreenshot(data) },
                    dialog = {
                        AddOrEditCategoryDialog(
                            state = MutableAddOrEditCategoryState().apply {
                                name = NEW_CATEGORY_NAME
                                color = colorItems[NEW_CATEGORY_COLOR_INDEX]
                            },
                            modifier = DialogInsetModifier
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryManagerScreenshot(data: ScreenshotData) {
    CategoryManagerScreen(
        state = MutableCategoryManagerState().apply {
            categories = data.categories
                .filterNotNull()
                .sortedBy { it.name }
                .toPersistentList()
        }
    )
}

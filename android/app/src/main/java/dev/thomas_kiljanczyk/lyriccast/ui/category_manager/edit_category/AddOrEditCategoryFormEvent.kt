/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.category_manager.edit_category

import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Category
import dev.thomas_kiljanczyk.lyriccast.domain.models.ColorItem
import java.util.UUID

sealed class AddOrEditCategoryFormEvent {
    data class CategoryInitialized(
        val category: Category
    ) : AddOrEditCategoryFormEvent()

    data class CategoryIdChanged(val id: UUID?) : AddOrEditCategoryFormEvent()
    data class CategoryNameChanged(val name: String) : AddOrEditCategoryFormEvent()
    data class CategoryColorChanged(val colorItem: ColorItem) :
        AddOrEditCategoryFormEvent()

    data object Submit : AddOrEditCategoryFormEvent()
}

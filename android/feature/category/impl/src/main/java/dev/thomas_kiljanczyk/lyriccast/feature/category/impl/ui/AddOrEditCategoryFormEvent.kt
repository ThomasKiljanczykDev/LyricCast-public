
package dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui

import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.ColorItem
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

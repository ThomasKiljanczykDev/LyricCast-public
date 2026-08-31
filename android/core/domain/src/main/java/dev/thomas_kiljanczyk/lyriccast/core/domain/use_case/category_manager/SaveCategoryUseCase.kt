package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.category_manager

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.R
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.SaveCategoryResult
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import java.util.UUID
import javax.inject.Inject

class SaveCategoryUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    suspend operator fun invoke(
        name: String,
        colorValue: Int,
        categoryId: UUID?
    ): SaveCategoryResult {
        return try {
            val category = Category(
                name = name.trim().uppercase(),
                color = colorValue,
                id = categoryId ?: UUIDv7.randomUUID()
            )

            categoriesRepository.upsertCategory(category)
            SaveCategoryResult.Success(category)
        } catch (e: Exception) {
            SaveCategoryResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.category_manager_save_failed)
            )
        }
    }
}

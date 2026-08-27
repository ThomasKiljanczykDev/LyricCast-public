/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.category_manager.DeleteCategoriesUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.DeleteCategoriesResult
import java.util.UUID
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface CategoryManagerState {
    val categories: ImmutableList<CategoryItem>
}

class MutableCategoryManagerState : CategoryManagerState {
    override var categories by mutableStateOf<PersistentList<CategoryItem>>(persistentListOf())
}

@HiltViewModel
class CategoryManagerViewModel @Inject constructor(
    categoriesRepository: CategoriesRepository,
    private val deleteCategoriesUseCase: DeleteCategoriesUseCase
) : ViewModel() {

    val state: CategoryManagerState
        field = MutableCategoryManagerState()

    init {
        categoriesRepository.getAllCategories()
            .onEach { categories ->
                state.categories = categories
                    .sortedBy { category -> category.name }
                    .map { category -> CategoryItem(category) }
                    .toPersistentList()
            }
            .launchIn(viewModelScope)
    }

    suspend fun deleteSelectedCategories(): DeleteCategoriesResult {
        val selectedCategoryIds = state.categories
            .filter { it.isSelected }
            .map { item -> item.id }

        return deleteCategoriesUseCase(selectedCategoryIds)
    }

    fun cancelSelection() {
        state.categories = state.categories.map { categoryItem ->
            categoryItem.copy(isSelected = false)
        }.toPersistentList()
    }

    fun selectCategory(categoryId: UUID, selected: Boolean) {
        val categoryIndex =
            state.categories.indexOfFirst { categoryItem -> categoryItem.id == categoryId }
        if (categoryIndex == -1) return

        val category = state.categories[categoryIndex]
        state.categories =
            state.categories.replacingAt(categoryIndex, category.copy(isSelected = selected))
    }
}

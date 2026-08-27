/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.category_manager.edit_category

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.category_manager.GetCategoryNamesUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.category_manager.SaveCategoryUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.category_manager.ValidateCategoryNameUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.ColorItem
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.colorItems
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface AddOrEditCategoryState {
    val id: UUID?
    val name: String
    val initialName: String?
    val nameError: UiText?
    val color: ColorItem
    val isValid: Boolean
}

class MutableAddOrEditCategoryState : AddOrEditCategoryState {
    override var id by mutableStateOf<UUID?>(null)
    override var name by mutableStateOf("")
    override var initialName by mutableStateOf<String?>(null)
    override var nameError by mutableStateOf<UiText?>(null)
    override var color by mutableStateOf(colorItems.first())
    override val isValid by derivedStateOf { nameError == null }
}

@HiltViewModel
class AddOrEditCategoryDialogViewModel @Inject constructor(
    getCategoryNamesUseCase: GetCategoryNamesUseCase,
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val validateCategoryNameUseCase: ValidateCategoryNameUseCase
) : ViewModel() {

    private val _state = MutableAddOrEditCategoryState()
    val state: AddOrEditCategoryState get() = _state

    private var categoryNamesState: Set<String> = setOf()

    init {
        getCategoryNamesUseCase()
            .onEach { newCategoryNames -> categoryNamesState = newCategoryNames }
            .flowOn(Dispatchers.Default).launchIn(viewModelScope)
    }

    suspend fun onEvent(event: AddOrEditCategoryFormEvent) {
        when (event) {
            is AddOrEditCategoryFormEvent.CategoryInitialized -> {
                val colorItem = colorItems.firstOrNull {
                    it.value == event.category.color
                } ?: colorItems.first()

                _state.apply {
                    id = event.category.id
                    name = event.category.name
                    initialName = event.category.name
                    color = colorItem
                }
            }

            is AddOrEditCategoryFormEvent.CategoryIdChanged -> {
                _state.id = event.id
            }

            is AddOrEditCategoryFormEvent.CategoryNameChanged -> {
                val newName = event.name.take(ValidateCategoryNameUseCase.MAX_LENGTH).uppercase()

                val initialName = _state.initialName
                val categoryNames = if (initialName == null) {
                    categoryNamesState
                } else {
                    categoryNamesState - initialName
                }

                val validationResult =
                    validateCategoryNameUseCase(newName, categoryNames)
                _state.apply {
                    name = newName
                    nameError = validationResult.errorMessage
                }
            }

            is AddOrEditCategoryFormEvent.CategoryColorChanged -> {
                _state.color = event.colorItem
            }

            is AddOrEditCategoryFormEvent.Submit -> {
                if (!_state.isValid) {
                    return
                }

                submit()
            }
        }
    }

    private suspend fun submit() {
        saveCategoryUseCase(_state.name, _state.color.value, _state.id)
    }
}

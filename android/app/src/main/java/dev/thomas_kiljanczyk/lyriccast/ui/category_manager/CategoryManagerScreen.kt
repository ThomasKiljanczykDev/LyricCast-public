/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 6:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 2:02 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.category_manager

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Category
import dev.thomas_kiljanczyk.lyriccast.domain.models.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.ui.category_manager.edit_category.AddOrEditCategoryDialog
import dev.thomas_kiljanczyk.lyriccast.ui.shared.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.ui.shared.theme.LyricCastTheme
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@Composable
fun CategoryManagerScreen(
    onNavigateUp: () -> Unit, viewModel: CategoryManagerViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    CategoryManagerScreen(
        state = viewModel.state,
        onCancelSelection = { viewModel.cancelSelection() },
        onItemSelected = { categoryItem, isSelected ->
            viewModel.selectCategory(categoryItem.id, isSelected)
        },
        onDelete = {
            scope.launch {
                viewModel.deleteSelectedCategories()
            }
        },
        onNavigateUp = onNavigateUp
    )
}

@Composable
fun CategoryManagerScreen(
    state: CategoryManagerState,
    onItemSelected: (CategoryItem, Boolean) -> Unit = { _, _ -> },
    onCancelSelection: () -> Unit = { },
    onDelete: () -> Unit = { },
    onNavigateUp: () -> Unit = { }
) {
    var categoryToEdit by remember { mutableStateOf<CategoryItem?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CategoryManagerTopBar(
                onNavigateUp = onNavigateUp,
                selectedCount = state.categories.count { it.isSelected },
                onCancelSelection = onCancelSelection,
                onDelete = onDelete,
                onEdit = {
                    val selectedCategory = state.categories.firstOrNull { it.isSelected }
                    categoryToEdit = selectedCategory
                    if (selectedCategory != null) {
                        showAddCategoryDialog = true
                        onItemSelected(
                            selectedCategory,
                            false
                        )
                    }
                },
                onAdd = {
                    showAddCategoryDialog = true
                })
        }) { paddingValues ->
        CategoryList(
            categories = state.categories,
            onCategorySelected = onItemSelected,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 4.dp)
        )
        if (showAddCategoryDialog) {
            AddOrEditCategoryDialog(
                category = categoryToEdit?.let {
                    Category(
                        id = it.id,
                        name = it.name,
                        color = it.color
                    )
                },
                onDismiss = {
                    showAddCategoryDialog = false
                })
        }
    }
}


@PreviewLightDark
@Composable
fun CategoryManagerScreenPreview() {
    LyricCastTheme {
        CategoryManagerScreen(
            state = MutableCategoryManagerState().apply {
                categories = PreviewData.createSampleCategoriesForManager(20).toPersistentList()
            }
        )
    }
}
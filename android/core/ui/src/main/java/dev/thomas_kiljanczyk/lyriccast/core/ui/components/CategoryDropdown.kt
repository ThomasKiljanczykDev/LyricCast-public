/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 6:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 2:02 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: ImmutableList<CategoryItem?>,
    selectedCategory: CategoryItem?,
    onCategorySelected: (CategoryItem?) -> Unit,
    modifier: Modifier = Modifier,
    nullCategoryText: String = stringResource(R.string.all_categories)
) {
    LyricCastSpinner(
        options = categories,
        value = selectedCategory?.name ?: nullCategoryText,
        label = stringResource(R.string.category_label),
        onOptionSelected = onCategorySelected,
        modifier = modifier
    ) {
        if (it == null) {
            Text(text = nullCategoryText)
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = it.color?.let { color ->
                    Color(
                        color
                    )
                } ?: Color.Transparent), modifier = Modifier
                    .height(30.dp)
                    .width(30.dp)) {}
                Text(text = it.name)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryDropdownPreview() {
    LyricCastTheme {
        Surface {
            CategoryDropdown(
                categories = persistentListOf(
                    CategoryItem(name = "Hymns"),
                    CategoryItem(name = "Contemporary"),
                    CategoryItem(name = "Pop")
                ),
                selectedCategory = null,
                onCategorySelected = {}
            )
        }
    }
}

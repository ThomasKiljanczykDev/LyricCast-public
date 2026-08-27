/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:28 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:26 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.category_manager

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CategoryListItem(
    item: CategoryItem,
    onCategorySelected: (CategoryItem, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    canClick: Boolean = false
) {
    val categoryColor = item.color
    val color = if (categoryColor != null && categoryColor != 0) Color(categoryColor) else null
    val animatedBorderColor by animateColorAsState(
        targetValue = if (!item.isSelected) Color.Transparent else (color
            ?: MaterialTheme.colorScheme.onPrimaryContainer)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(
                width = 3.dp, color = animatedBorderColor, shape = CardDefaults.shape
            )
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = {
                    if (canClick) {
                        onCategorySelected(item, !item.isSelected)
                    }
                },
                onLongClick = {
                    onCategorySelected(item, !item.isSelected)
                },
                indication = ripple(true),
                interactionSource = remember { MutableInteractionSource() })

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .height(48.dp)
        ) {
            Text(
                text = item.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            )

            if (color != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = color
                    ), modifier = Modifier
                        .width(60.dp)
                        .height(30.dp)
                        .padding(end = 8.dp)

                ) {}
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryListItemCheckboxSelectedPreview() {
    LyricCastTheme {
        Surface {
            CategoryListItem(item = CategoryItem(
                name = "Sample Category",
                color = R.color.red,
                isSelected = true
            ), onCategorySelected = { _, _ -> })
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryListItemCheckboxNotSelectedPreview() {
    LyricCastTheme {
        Surface {
            CategoryListItem(item = CategoryItem(
                name = "Sample Category",
                color = R.color.red,
                isSelected = false
            ), onCategorySelected = { _, _ -> })
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryListItemNoCheckboxPreview() {
    LyricCastTheme {
        Surface {
            CategoryListItem(item = CategoryItem(
                name = "Sample Category",
                color = R.color.red,
                isSelected = false
            ), onCategorySelected = { _, _ -> })
        }
    }
}

@Composable
fun CategoryList(
    categories: List<CategoryItem>,
    onCategorySelected: (CategoryItem, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val anyCategorySelected = categories.any { it.isSelected }
    LazyColumn(
        modifier = modifier
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryListItem(
                item = category,
                onCategorySelected = onCategorySelected,
                canClick = anyCategorySelected,
                modifier = Modifier.animateItem()
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryListPreview() {
    LyricCastTheme {
        Surface {
            CategoryList(categories = List(3) { index ->
                CategoryItem(
                    name = "Category $index",
                    color = R.color.red
                )
            }, onCategorySelected = { _, _ -> })
        }
    }
}

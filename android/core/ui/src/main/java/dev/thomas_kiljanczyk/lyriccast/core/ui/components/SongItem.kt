/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 7:11 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 6:24 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    songItem: SongItem,
    onToggleSelection: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = {}
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (songItem.isSelected) MaterialTheme.colorScheme.onPrimaryContainer
        else Color.Transparent, label = "border_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 3.dp, color = animatedBorderColor, shape = CardDefaults.shape
            )
            .clip(CardDefaults.shape)
            .combinedClickable(onClick = { onToggleSelection() }, onLongClick = { onLongClick() }),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = songItem.title, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }, supportingContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val categoryColor = songItem.category?.color
                    CategoryPill(
                        categoryName = songItem.category?.name ?: "",
                        categoryColor = if (categoryColor != null) Color(
                            categoryColor
                        ) else null
                    )
                }
            }, colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

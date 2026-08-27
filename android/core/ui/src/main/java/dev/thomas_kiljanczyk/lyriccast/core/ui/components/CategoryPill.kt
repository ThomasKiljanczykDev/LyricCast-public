/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 7:11 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 6:24 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import dev.thomas_kiljanczyk.lyriccast.core.ui.R

@Composable
fun CategoryPill(
    categoryName: String,
    categoryColor: Color?,
    modifier: Modifier = Modifier
) {
    val brightTextColor = colorResource(R.color.bright_text)
    val darkTextColor = colorResource(R.color.dark_text)

    val backgroundColor = categoryColor?.toArgb()
    val brightTextContrast = if (backgroundColor != null)
        ColorUtils.calculateContrast(brightTextColor.toArgb(), backgroundColor) else 0.0
    val darkTextContrast = if (backgroundColor != null)
        ColorUtils.calculateContrast(darkTextColor.toArgb(), backgroundColor)
    else 0.0

    val textColor = if (brightTextContrast > darkTextContrast) {
        brightTextColor
    } else {
        darkTextColor
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = categoryColor ?: Color.Unspecified
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = categoryName,
            modifier = Modifier.padding(6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

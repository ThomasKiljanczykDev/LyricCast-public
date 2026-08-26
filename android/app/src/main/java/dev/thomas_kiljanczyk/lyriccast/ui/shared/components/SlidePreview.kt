/*
 * Created by Tomasz Kiljanczyk on 8/29/25, 1:41 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 8/29/25, 1:38 AM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.shared.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.thomas_kiljanczyk.lyriccast.ui.shared.theme.LyricCastTheme

@Composable
fun SlidePreview(
    slideText: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 16
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = slideText,
                fontSize = fontSize.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSlidePreview() {
    LyricCastTheme {
        Surface {
            SlidePreview(
                slideText = "Amazing grace, how sweet the sound\nThat saved a wretch like me\n" +
                    "I once was lost, but now am found\nWas blind, but now I see"
            )
        }
    }
}

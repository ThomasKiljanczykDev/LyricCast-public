package dev.thomas_kiljanczyk.lyriccast.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme

@Composable
fun SongInfo(
    songTitle: String,
    currentSlide: Int,
    totalSlideCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = songTitle,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${currentSlide + 1}/$totalSlideCount",
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewSongInfo() {
    LyricCastTheme {
        Surface {
            SongInfo(
                songTitle = "Amazing Grace",
                currentSlide = 2,
                totalSlideCount = 5
            )
        }
    }
}

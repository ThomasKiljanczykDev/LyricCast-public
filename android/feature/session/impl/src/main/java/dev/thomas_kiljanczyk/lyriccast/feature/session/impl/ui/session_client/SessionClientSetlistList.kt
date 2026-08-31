
package dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.R
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.shared.preview.SessionPreviewData

/**
 * The presenter's running order, shown read-only: a session client follows along and cannot
 * drive song selection, so entries are not clickable.
 *
 * Keyed by song id rather than index so the highlight animates correctly when the presenter
 * loads a different setlist.
 */
@Composable
fun SessionClientSetlistList(
    setlist: SetlistInfo,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Follow the presenter: keep the song they are on in view as they move through the setlist.
    LaunchedEffect(setlist.currentSongIndex) {
        if (setlist.currentSongIndex in setlist.songs.indices) {
            listState.animateScrollToItem(setlist.currentSongIndex)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            itemsIndexed(setlist.songs, key = { _, song -> song.id }) { index, song ->
                SessionClientSetlistItem(
                    position = index + 1,
                    title = song.title,
                    isCurrent = index == setlist.currentSongIndex,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun SessionClientSetlistItem(
    position: Int,
    title: String,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isCurrent) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        label = "containerColor"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (isCurrent) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "textColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = animatedContainerColor)
    ) {
        Text(
            text = stringResource(
                R.string.session_client_song_item_title_template,
                position,
                title
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = animatedTextColor
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewSessionClientSetlistList() {
    LyricCastTheme {
        Surface {
            SessionClientSetlistList(setlist = SessionPreviewData.sampleSetlist)
        }
    }
}

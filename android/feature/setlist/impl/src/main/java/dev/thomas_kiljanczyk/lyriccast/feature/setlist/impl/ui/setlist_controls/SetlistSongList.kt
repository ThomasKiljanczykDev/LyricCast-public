
package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_controls

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.R

@Composable
fun SongListItem(
    songItem: SongItem,
    position: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        label = "containerColor"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "textColor"
    )

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = animatedContainerColor
        )
    ) {
        Text(
            text = stringResource(
                R.string.setlist_controls_song_item_title_template,
                position,
                songItem.title
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

@Composable
fun SetlistSongList(
    songs: List<SongItem>,
    listState: LazyListState,
    currentSongIndex: Int,
    onSongClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
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
            items(songs.size) { index ->
                SongListItem(
                    songItem = songs[index],
                    position = index + 1,
                    isSelected = index == currentSongIndex,
                    onClick = { onSongClick(index) },
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSongListItem() {
    LyricCastTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SongListItem(
                    songItem = SongItem.fromSong(
                        Song(
                            title = "Amazing Grace",
                            lyrics = listOf(
                                Song.LyricsSection(
                                    "Verse 1",
                                    "Amazing grace, how sweet the sound"
                                )
                            ),
                            presentation = listOf("Verse 1")
                        ), false
                    ),
                    position = 1,
                    isSelected = true,
                    onClick = {}
                )

                SongListItem(
                    songItem = SongItem.fromSong(
                        Song(
                            title = "How Great Thou Art",
                            lyrics = listOf(
                                Song.LyricsSection(
                                    "Verse 1",
                                    "O Lord my God, when I in awesome wonder"
                                )
                            ),
                            presentation = listOf("Verse 1")
                        ), false
                    ),
                    position = 2,
                    isSelected = false,
                    onClick = {}
                )

                SongListItem(
                    songItem = SongItem.fromSong(
                        Song(
                            title = "Blessed Assurance, Jesus Is Mine",
                            lyrics = listOf(
                                Song.LyricsSection(
                                    "Verse 1",
                                    "Blessed assurance, Jesus is mine"
                                )
                            ),
                            presentation = listOf("Verse 1")
                        ), false
                    ),
                    position = 3,
                    isSelected = false,
                    onClick = {}
                )
            }
        }
    }
}

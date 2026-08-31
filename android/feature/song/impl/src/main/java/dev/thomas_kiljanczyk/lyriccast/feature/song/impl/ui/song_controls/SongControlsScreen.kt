
package dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.core.cast.ui.CastButton
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackState
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.ControlButtons
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SlidePreview
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SongInfo
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.currentWindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.isWidthExpanded
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.R
import java.util.UUID
import kotlinx.coroutines.launch

/** Side-pane width for title and controls on expanded-width windows. */
private val CONTROLS_COLUMN_WIDTH = 400.dp

@Composable
fun SongControlsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
    songId: UUID,
    viewModel: SongControlsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(songId) {
        viewModel.loadSong(songId)
        viewModel.sendSlide()
    }

    SongControlsScreen(
        state = state,
        onNavigateUp = onNavigateUp,
        onNavigateToSettings = onNavigateToSettings,
        onPreviousClick = {
            coroutineScope.launch {
                viewModel.goToPreviousSlide()
            }
        },
        onNextClick = {
            coroutineScope.launch {
                viewModel.goToNextSlide()
            }
        },
        onBlankClick = {
            coroutineScope.launch {
                viewModel.sendBlank()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongControlsScreen(
    state: PlaybackState,
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onBlankClick: () -> Unit,
    windowSizeClass: WindowSizeClass = currentWindowSizeClass()
) {
    @Composable
    fun SongInfoSection(infoModifier: Modifier = Modifier) {
        SongInfo(
            songTitle = state.songTitle,
            currentSlide = state.currentSlide,
            totalSlideCount = state.totalSlideCount,
            modifier = infoModifier.padding(horizontal = 4.dp)
        )
    }

    @Composable
    fun Controls(controlsModifier: Modifier = Modifier) {
        ControlButtons(
            buttonHeight = state.buttonHeight,
            isBlanked = state.isBlanked,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
            onBlankClick = onBlankClick,
            isPreviousEnabled = state.currentSlide > 0,
            isNextEnabled = state.currentSlide < state.totalSlideCount - 1,
            isBlankEnabled = state.isCastConnected,
            modifier = controlsModifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_controls)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    CastButton(size = 48.dp)
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(R.string.settings_title)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (windowSizeClass.isWidthExpanded()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SlidePreview(
                    slideText = state.currentSlideText,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    fontSize = 18
                )

                Column(
                    modifier = Modifier
                        .width(CONTROLS_COLUMN_WIDTH)
                        .fillMaxHeight()
                ) {
                    SongInfoSection()

                    Spacer(modifier = Modifier.weight(1f))

                    Controls()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    SongInfoSection()

                    SlidePreview(
                        slideText = state.currentSlideText,
                        modifier = Modifier.weight(1f),
                        fontSize = 18
                    )
                }

                Controls()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSongControlsScreen() {
    LyricCastTheme {
        SongControlsScreen(
            state = PlaybackState(
                songTitle = "Amazing Grace",
                currentSlideText = "Amazing grace, how sweet the sound\n" +
                    "That saved a wretch like me\n" +
                    "I once was lost, but now am found\n" +
                    "Was blind, but now I see",
                currentSlide = 0,
                totalSlideCount = 5,
                isBlanked = false
            ),
            onNavigateUp = {},
            onNavigateToSettings = {},
            onPreviousClick = {},
            onNextClick = {},
            onBlankClick = {}
        )
    }
}

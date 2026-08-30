/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 6:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 2:02 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_controls

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalView
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
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.currentWindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.isWidthExpanded
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.R
import java.util.UUID
import kotlinx.coroutines.launch

/** Side-pane width for song info, running order and controls on expanded-width windows. */
private val CONTROLS_COLUMN_WIDTH = 400.dp

@Composable
fun SetlistControlsScreen(
    setlistId: UUID,
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: SetlistControlsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(setlistId) {
        viewModel.loadSetlist(setlistId)
        viewModel.sendSlide()
    }

    SetlistControlsScreen(
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
        },
        onSongClick = { position ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
            coroutineScope.launch {
                viewModel.selectSong(position, true)
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistControlsScreen(
    state: PlaybackState,
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onBlankClick: () -> Unit,
    onSongClick: (Int) -> Unit,
    windowSizeClass: WindowSizeClass = currentWindowSizeClass()
) {
    val listState = rememberLazyListState()

    // Auto-scroll to current song when position changes
    LaunchedEffect(state.currentSongPosition) {
        listState.animateScrollToItem(state.currentSongPosition)
    }

    @Composable
    fun SongInfoSection() {
        SongInfo(
            songTitle = state.songTitle,
            currentSlide = state.currentSlide,
            totalSlideCount = state.totalSlideCount,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }

    @Composable
    fun SongList(listModifier: Modifier = Modifier) {
        SetlistSongList(
            songs = state.songs,
            listState = listState,
            currentSongIndex = state.currentSongPosition,
            onSongClick = onSongClick,
            modifier = listModifier
        )
    }

    @Composable
    fun Controls() {
        ControlButtons(
            buttonHeight = state.buttonHeight,
            isBlanked = state.isBlanked,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
            onBlankClick = onBlankClick,
            isPreviousEnabled = state.currentSlide > 0 || state.currentSongPosition > 0,
            isNextEnabled = state.currentSlide < state.totalSlideCount - 1 ||
                state.currentSongPosition < state.songs.size - 1,
            isBlankEnabled = state.isCastConnected,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.title_controls)) }, navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        }, actions = {
            CastButton(size = 48.dp)
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.settings_title)
                )
            }
        })
    }) { paddingValues ->
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
                        .fillMaxHeight()
                )

                Column(
                    modifier = Modifier
                        .width(CONTROLS_COLUMN_WIDTH)
                        .fillMaxHeight()
                ) {
                    SongInfoSection()

                    SongList(Modifier.weight(1f))

                    Controls()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SongInfoSection()

                SlidePreview(
                    slideText = state.currentSlideText,
                    modifier = Modifier.weight(1f)
                )

                SongList(Modifier.weight(1f))

                Controls()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSetlistControlsScreen() {
    LyricCastTheme {
        SetlistControlsScreen(
            state = PlaybackState(
                songs = PreviewData.sampleSongsWithLyrics,
                currentSlideText = "Sample lyrics text\nWith multiple lines\nFor preview purposes",
                songTitle = "Sample Song 2",
                currentSlide = 1,
                totalSlideCount = 4,
                currentSongPosition = 1,
                isBlanked = false
            ),
            onNavigateUp = {},
            onNavigateToSettings = {},
            onPreviousClick = {},
            onNextClick = {},
            onBlankClick = {},
            onSongClick = {})
    }
}

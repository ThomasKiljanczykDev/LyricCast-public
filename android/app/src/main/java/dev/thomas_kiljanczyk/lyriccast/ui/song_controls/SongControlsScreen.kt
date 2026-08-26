/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.song_controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.ui.shared.components.CastButton
import dev.thomas_kiljanczyk.lyriccast.ui.shared.components.ControlButtons
import dev.thomas_kiljanczyk.lyriccast.ui.shared.components.SlidePreview
import dev.thomas_kiljanczyk.lyriccast.ui.shared.components.SongInfo
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.ui.shared.theme.LyricCastTheme
import java.util.UUID
import kotlinx.coroutines.launch

@Composable
fun SongControlsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
    songId: UUID,
    viewModel: SongControlsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(songId) {
        viewModel.loadSong(songId)
        viewModel.sendSlide()
    }

    SongControlsScreen(
        state = viewModel.state,
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
    state: SongControlsState,
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onBlankClick: () -> Unit
) {
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
                SongInfo(
                    songTitle = state.songTitle,
                    currentSlide = state.currentSlide,
                    totalSlideCount = state.totalSlideCount,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                SlidePreview(
                    slideText = state.currentSlideText,
                    modifier = Modifier.weight(1f),
                    fontSize = 18
                )
            }

            ControlButtons(
                buttonHeight = state.buttonHeight,
                isBlanked = state.isBlanked,
                onPreviousClick = onPreviousClick,
                onNextClick = onNextClick,
                onBlankClick = onBlankClick,
                isPreviousEnabled = state.currentSlide > 0,
                isNextEnabled = state.currentSlide < state.totalSlideCount - 1,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSongControlsScreen() {
    LyricCastTheme {
        SongControlsScreen(
            state = MutableSongControlsState().apply {
                songTitle = "Amazing Grace"
                currentSlideText =
                    "Amazing grace, how sweet the sound\nThat saved a wretch like me\nI once was lost, but now am found\nWas blind, but now I see"
                currentSlide = 0
                totalSlideCount = 5
                isBlanked = false
                buttonHeight = ControlButtonHeightOption.DEFAULT.value
            },
            onNavigateUp = {},
            onNavigateToSettings = {},
            onPreviousClick = {},
            onNextClick = {},
            onBlankClick = {}
        )
    }
}

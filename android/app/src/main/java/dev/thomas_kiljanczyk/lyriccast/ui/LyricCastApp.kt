/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 8:20 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 8:17 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.settings.SettingsRepository
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ThemeOption
import dev.thomas_kiljanczyk.lyriccast.navigation.LyricCastNavHost
import dev.thomas_kiljanczyk.lyriccast.navigation.rememberLyricCastAppState
import dev.thomas_kiljanczyk.lyriccast.tutorial.TutorialPhase
import dev.thomas_kiljanczyk.lyriccast.tutorial.TutorialViewModel
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun LyricCastApp(
    activity: ComponentActivity,
    settingsRepository: SettingsRepository,
    warmIntents: SharedFlow<Intent>,
    modifier: Modifier = Modifier,
) {
    val appTheme by settingsRepository.appTheme.collectAsStateWithLifecycle(initialValue = null)
    val themeOption = appTheme?.let { ThemeOption.fromValue(it) }

    val appState = rememberLyricCastAppState()

    // Navigation resolves a cold-start lyriccast:// deep link on its own via the manifest
    // intent-filter; a warm intent delivered to an already-running Activity has to be replayed
    // into the existing NavController by hand.
    LaunchedEffect(appState.navController) {
        warmIntents.collect { intent -> appState.navController.handleDeepLink(intent) }
    }

    val tutorialViewModel: TutorialViewModel = hiltViewModel()

    // Avoid drawing before the persisted theme is known, otherwise the first frame renders in the
    // wrong theme and flashes once DataStore delivers the real value.
    ReportDrawnWhen { appTheme != null }

    LyricCastTheme(themeOption = themeOption) {
        if (tutorialViewModel.phase != TutorialPhase.LOADING) {
            LyricCastNavHost(
                appState = appState,
                activity = activity,
                modifier = modifier.fillMaxSize(),
                startDestination = tutorialViewModel.startDestination,
                onOnboardingComplete = tutorialViewModel::finish,
                onOnboardingSkip = tutorialViewModel::finish,
            )
        }
    }
}

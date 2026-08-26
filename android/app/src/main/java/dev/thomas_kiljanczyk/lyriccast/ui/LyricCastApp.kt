/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 8:20 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 8:17 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.thomas_kiljanczyk.lyriccast.application.AppSettings
import dev.thomas_kiljanczyk.lyriccast.data.SettingsRepository
import dev.thomas_kiljanczyk.lyriccast.navigation.LyricCastNavHost
import dev.thomas_kiljanczyk.lyriccast.navigation.rememberLyricCastAppState
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings.ThemeOption
import dev.thomas_kiljanczyk.lyriccast.ui.shared.theme.LyricCastTheme

@Composable
fun LyricCastApp(
    activity: ComponentActivity,
    settingsRepository: SettingsRepository,
    modifier: Modifier = Modifier,
) {
    val appSettings by settingsRepository.getAllSettings()
        .collectAsState(initial = AppSettings.getDefaultInstance())
    val themeOption = ThemeOption.fromValue(appSettings.appTheme)

    val appState = rememberLyricCastAppState()

    LyricCastTheme(themeOption = themeOption) {
        LyricCastNavHost(
            appState = appState,
            activity = activity,
            modifier = modifier.fillMaxSize(),
        )
    }
}

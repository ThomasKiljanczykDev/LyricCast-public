/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:14 PM
 */

package dev.thomas_kiljanczyk.lyriccast.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.navigation.CategoryManagerRoute
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.navigation.SessionClientRoute
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.navigation.SetlistControlsRoute
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.navigation.SetlistEditorRoute
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.navigation.SettingsRoute
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.navigation.SongControlsRoute
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.navigation.SongEditorRoute
import java.util.UUID
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberLyricCastAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): LyricCastAppState {
    return remember(
        navController,
        coroutineScope,
    ) {
        LyricCastAppState(
            navController = navController,
        )
    }
}

@Stable
class LyricCastAppState(
    val navController: NavHostController,
) {
    fun navigateToSettings() {
        navController.navigate(SettingsRoute) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToSongEditor(songId: UUID? = null) {
        navController.navigate(SongEditorRoute(songId = songId?.toString())) {
            launchSingleTop = true
        }
    }

    fun navigateToSetlistEditor(setlistId: UUID? = null, presentation: List<UUID>? = null) {
        navController.navigate(
            SetlistEditorRoute(
                setlistId = setlistId?.toString(),
                presentation = presentation?.map { it.toString() }
            )
        ) {
            launchSingleTop = true
        }
    }

    fun navigateToCategoryManager() {
        navController.navigate(CategoryManagerRoute) {
            launchSingleTop = true
        }
    }

    fun navigateToSessionClient() {
        navController.navigate(SessionClientRoute) {
            launchSingleTop = true
        }
    }

    fun navigateToSongControls(songId: UUID) {
        navController.navigate(SongControlsRoute(songId = songId.toString())) {
            launchSingleTop = true
        }
    }

    fun navigateToSetlistControls(setlistId: UUID) {
        navController.navigate(SetlistControlsRoute(setlistId = setlistId.toString())) {
            launchSingleTop = true
        }
    }
}

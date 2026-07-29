/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:14 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.song.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import dev.thomas_kiljanczyk.lyriccast.navigation.DeepLinkConstants
import dev.thomas_kiljanczyk.lyriccast.navigation.SongControlsRoute
import dev.thomas_kiljanczyk.lyriccast.navigation.SongEditorRoute
import dev.thomas_kiljanczyk.lyriccast.ui.song_controls.SongControlsScreen
import dev.thomas_kiljanczyk.lyriccast.ui.song_editor.SongEditorScreen
import java.util.UUID

fun NavController.navigateToSongEditor(songId: UUID? = null, navOptions: NavOptions? = null) =
    navigate(route = SongEditorRoute(songId = songId?.toString()), navOptions)

fun NavController.navigateToSongControls(songId: UUID, navOptions: NavOptions? = null) =
    navigate(route = SongControlsRoute(songId = songId.toString()), navOptions)

fun NavGraphBuilder.songEditorScreen(
    onNavigateUp: () -> Unit,
) {
    composable<SongEditorRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLinkConstants.SONG_EDITOR_PATTERN
            }
        )
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<SongEditorRoute>()
        val songId = route.songId?.let { UUID.fromString(it) }

        SongEditorScreen(
            songId = songId,
            onNavigateUp = onNavigateUp,
        )
    }
}

fun NavGraphBuilder.songControlsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable<SongControlsRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLinkConstants.SONG_CONTROLS_PATTERN
            }
        )
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<SongControlsRoute>()
        val songId = UUID.fromString(route.songId)

        SongControlsScreen(
            songId = songId,
            onNavigateUp = onNavigateUp,
            onNavigateToSettings = onNavigateToSettings,
        )
    }
}
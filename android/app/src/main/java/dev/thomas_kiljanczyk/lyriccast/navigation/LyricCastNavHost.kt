/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:49 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:44 PM
 */

package dev.thomas_kiljanczyk.lyriccast.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import dev.thomas_kiljanczyk.lyriccast.feature.category.navigation.categoryManagerScreen
import dev.thomas_kiljanczyk.lyriccast.feature.category.navigation.navigateToCategoryManager
import dev.thomas_kiljanczyk.lyriccast.feature.main.navigation.mainSection
import dev.thomas_kiljanczyk.lyriccast.feature.session.navigation.navigateToSessionClient
import dev.thomas_kiljanczyk.lyriccast.feature.session.navigation.sessionClientScreen
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.navigation.navigateToSetlistControls
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.navigation.navigateToSetlistEditor
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.navigation.setlistControlsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.navigation.setlistEditorScreen
import dev.thomas_kiljanczyk.lyriccast.feature.settings.navigation.navigateToSettings
import dev.thomas_kiljanczyk.lyriccast.feature.settings.navigation.settingsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.song.navigation.navigateToSongControls
import dev.thomas_kiljanczyk.lyriccast.feature.song.navigation.navigateToSongEditor
import dev.thomas_kiljanczyk.lyriccast.feature.song.navigation.songControlsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.song.navigation.songEditorScreen
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

@Serializable
data object SettingsRoute

@Serializable
data class SongEditorRoute(
    val songId: String? = null
)

@Serializable
data class SetlistEditorRoute(
    val setlistId: String? = null,
    val presentation: List<String>? = null
)

@Serializable
data object CategoryManagerRoute

@Serializable
data object SessionClientRoute

@Serializable
data class SongControlsRoute(val songId: String)

@Serializable
data class SetlistControlsRoute(val setlistId: String)

/**
 * Top-level navigation graph. Navigation is organized using feature-based modules
 * following the Now in Android architecture patterns.
 *
 * @param appState Application state containing navigation controller and app-level state
 * @param activity Component activity reference for screens that require it
 * @param modifier Modifier for the NavHost
 * @param startDestination Starting destination for the navigation graph
 */
@Composable
fun LyricCastNavHost(
    appState: LyricCastAppState,
    activity: ComponentActivity,
    modifier: Modifier = Modifier,
    startDestination: Any = MainRoute,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Main screen with nested destinations
        mainSection(
            activity = activity,
            onNavigateToSettings = navController::navigateToSettings,
            onNavigateToSongEditor = navController::navigateToSongEditor,
            onNavigateToSetlistEditor = navController::navigateToSetlistEditor,
            onNavigateToCategoryManager = navController::navigateToCategoryManager,
            onNavigateToSessionClient = navController::navigateToSessionClient,
            onNavigateToSongControls = navController::navigateToSongControls,
            onNavigateToSetlistControls = navController::navigateToSetlistControls,
        )

        // Settings screen
        settingsScreen(
            onNavigateUp = navController::popBackStack,
        )

        // Song-related screens
        songEditorScreen(
            onNavigateUp = navController::popBackStack,
        )

        songControlsScreen(
            onNavigateUp = navController::popBackStack,
            onNavigateToSettings = navController::navigateToSettings,
        )

        // Setlist-related screens
        setlistEditorScreen(
            onNavigateBack = navController::popBackStack,
        )

        setlistControlsScreen(
            onNavigateUp = navController::popBackStack,
            onNavigateToSettings = navController::navigateToSettings,
        )

        // Category management screen
        categoryManagerScreen(
            onNavigateUp = navController::popBackStack,
        )

        // Session client screen
        sessionClientScreen(
            onNavigateUp = navController::popBackStack,
        )
    }
}

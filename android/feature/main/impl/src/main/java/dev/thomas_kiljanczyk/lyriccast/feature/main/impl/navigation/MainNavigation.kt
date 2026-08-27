/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:11 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.navigation

import androidx.activity.ComponentActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.MainScreen
import java.util.UUID
import kotlinx.serialization.Serializable

/**
 * This module's own main route, kept local so it does not depend on the app-level nav graph.
 * The app-level NavHost owns the shared `MainRoute`/`DeepLinkConstants` and is wired up
 * separately.
 */
@Serializable
data object MainRoute

@Serializable
data object MainBaseRoute

/** Deep link constants for the main screen, kept local to this module. */
object MainDeepLinks {
    const val SCHEME = "lyriccast"
    const val HOST = "app"
    const val MAIN_PATTERN = "$SCHEME://$HOST/main"
}

fun NavController.navigateToMain(navOptions: NavOptions? = null) =
    navigate(route = MainRoute, navOptions)

fun NavGraphBuilder.mainSection(
    activity: ComponentActivity,
    onNavigateToSettings: () -> Unit,
    onNavigateToSongEditor: (UUID?) -> Unit,
    onNavigateToSetlistEditor: (UUID?, List<UUID>?) -> Unit,
    onNavigateToCategoryManager: () -> Unit,
    onNavigateToSessionClient: () -> Unit,
    onNavigateToSongControls: (UUID) -> Unit,
    onNavigateToSetlistControls: (UUID) -> Unit,
    onStartSessionServer: () -> Unit = {},
) {
    composable<MainRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = MainDeepLinks.MAIN_PATTERN
            }
        )
    ) {
        MainScreen(
            activity = activity,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToSongEditor = onNavigateToSongEditor,
            onNavigateToSetlistEditor = onNavigateToSetlistEditor,
            onNavigateToCategoryManager = onNavigateToCategoryManager,
            onNavigateToSessionClient = onNavigateToSessionClient,
            onNavigateToSongControls = onNavigateToSongControls,
            onNavigateToSetlistControls = onNavigateToSetlistControls,
            onStartSessionServer = onStartSessionServer,
        )
    }
}

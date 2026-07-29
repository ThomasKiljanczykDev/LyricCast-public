/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:11 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.main.navigation

import androidx.activity.ComponentActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import dev.thomas_kiljanczyk.lyriccast.navigation.DeepLinkConstants
import dev.thomas_kiljanczyk.lyriccast.navigation.MainRoute
import dev.thomas_kiljanczyk.lyriccast.ui.main.MainScreen
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data object MainBaseRoute

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
) {
    composable<MainRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLinkConstants.MAIN_PATTERN
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
        )
    }
}
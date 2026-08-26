/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:13 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.setlist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import dev.thomas_kiljanczyk.lyriccast.navigation.DeepLinkConstants
import dev.thomas_kiljanczyk.lyriccast.navigation.SetlistControlsRoute
import dev.thomas_kiljanczyk.lyriccast.navigation.SetlistEditorRoute
import dev.thomas_kiljanczyk.lyriccast.ui.setlist_controls.SetlistControlsScreen
import dev.thomas_kiljanczyk.lyriccast.ui.setlist_editor.SetlistEditorScreen
import java.util.UUID

fun NavController.navigateToSetlistEditor(
    setlistId: UUID? = null,
    presentation: List<UUID>? = null,
    navOptions: NavOptions? = null
) = navigate(
    route = SetlistEditorRoute(
        setlistId = setlistId?.toString(),
        presentation = presentation?.map { it.toString() }),
    navOptions
)

fun NavController.navigateToSetlistControls(setlistId: UUID, navOptions: NavOptions? = null) =
    navigate(route = SetlistControlsRoute(setlistId = setlistId.toString()), navOptions)

fun NavGraphBuilder.setlistEditorScreen(
    onNavigateBack: () -> Unit,
) {
    composable<SetlistEditorRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLinkConstants.SETLIST_EDITOR_PATTERN
            }
        ),
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<SetlistEditorRoute>()
        val setlistId = route.setlistId?.let { UUID.fromString(it) }
        val presentation = route.presentation?.map { UUID.fromString(it) }

        SetlistEditorScreen(
            setlistId = setlistId,
            presentation = presentation,
            onNavigateBack = onNavigateBack,
        )
    }
}

fun NavGraphBuilder.setlistControlsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable<SetlistControlsRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLinkConstants.SETLIST_CONTROLS_PATTERN
            }
        ),
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<SetlistControlsRoute>()
        val setlistId = UUID.fromString(route.setlistId)

        SetlistControlsScreen(
            setlistId = setlistId,
            onNavigateUp = onNavigateUp,
            onNavigateToSettings = onNavigateToSettings,
        )
    }
}

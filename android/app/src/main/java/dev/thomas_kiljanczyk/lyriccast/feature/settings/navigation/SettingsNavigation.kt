/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 8:20 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 8:16 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import dev.thomas_kiljanczyk.lyriccast.navigation.DeepLinkConstants
import dev.thomas_kiljanczyk.lyriccast.navigation.SettingsRoute
import dev.thomas_kiljanczyk.lyriccast.ui.settings.SettingsScreen

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(route = SettingsRoute, navOptions)

fun NavGraphBuilder.settingsScreen(
    onNavigateUp: () -> Unit,
) {
    composable<SettingsRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLinkConstants.SETTINGS_PATTERN
            }
        ),
    ) {
        SettingsScreen(
            onNavigateUp = onNavigateUp,
        )
    }
}
/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 8:20 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 8:16 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.ui.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

object SettingsDeepLinks {
    const val SCHEME = "lyriccast"
    const val HOST = "app"
    const val SETTINGS_PATTERN = "$SCHEME://$HOST/settings"
}

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(route = SettingsRoute, navOptions)

fun NavGraphBuilder.settingsScreen(
    onNavigateUp: () -> Unit,
) {
    composable<SettingsRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = SettingsDeepLinks.SETTINGS_PATTERN
            }
        ),
    ) {
        SettingsScreen(
            onNavigateUp = onNavigateUp,
        )
    }
}

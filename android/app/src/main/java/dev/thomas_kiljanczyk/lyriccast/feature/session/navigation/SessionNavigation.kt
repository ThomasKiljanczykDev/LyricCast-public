/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 8:20 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 8:16 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.session.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import dev.thomas_kiljanczyk.lyriccast.navigation.DeepLinkConstants
import dev.thomas_kiljanczyk.lyriccast.navigation.SessionClientRoute
import dev.thomas_kiljanczyk.lyriccast.ui.session_client.SessionClientScreen

fun NavController.navigateToSessionClient(navOptions: NavOptions? = null) =
    navigate(route = SessionClientRoute, navOptions)

fun NavGraphBuilder.sessionClientScreen(
    onNavigateUp: () -> Unit,
) {
    composable<SessionClientRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLinkConstants.SESSION_CLIENT_PATTERN
            }
        ),
    ) {
        SessionClientScreen(
            onNavigateUp = onNavigateUp,
        )
    }
}
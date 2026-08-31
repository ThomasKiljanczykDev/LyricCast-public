
package dev.thomas_kiljanczyk.lyriccast.feature.session.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client.SessionClientScreen
import kotlinx.serialization.Serializable

@Serializable
data object SessionClientRoute

object SessionDeepLinks {
    const val SCHEME = "lyriccast"
    const val HOST = "app"
    const val SESSION_CLIENT_PATTERN = "$SCHEME://$HOST/session"
}

fun NavController.navigateToSessionClient(navOptions: NavOptions? = null) =
    navigate(route = SessionClientRoute, navOptions)

fun NavGraphBuilder.sessionClientScreen(
    onNavigateUp: () -> Unit,
) {
    composable<SessionClientRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = SessionDeepLinks.SESSION_CLIENT_PATTERN
            }
        ),
    ) {
        SessionClientScreen(
            onNavigateUp = onNavigateUp,
            viewModel = hiltViewModel(key = SessionClientRoute.toString()),
        )
    }
}

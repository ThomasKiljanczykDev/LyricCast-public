
package dev.thomas_kiljanczyk.lyriccast.feature.song.impl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_controls.SongControlsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_editor.SongEditorScreen
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class SongEditorRoute(val songId: String? = null)

@Serializable
data class SongControlsRoute(val songId: String)

object SongDeepLinks {
    const val SCHEME = "lyriccast"
    const val HOST = "app"

    const val SONG_EDITOR_PATTERN = "$SCHEME://$HOST/song/editor?songId={songId}"
    const val SONG_CONTROLS_PATTERN = "$SCHEME://$HOST/song/controls/{songId}"
}

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
                uriPattern = SongDeepLinks.SONG_EDITOR_PATTERN
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
                uriPattern = SongDeepLinks.SONG_CONTROLS_PATTERN
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

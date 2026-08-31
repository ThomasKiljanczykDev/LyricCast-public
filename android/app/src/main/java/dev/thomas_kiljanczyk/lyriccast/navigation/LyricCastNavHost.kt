package dev.thomas_kiljanczyk.lyriccast.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.navigation.categoryManagerScreen
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.navigation.navigateToCategoryManager
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.navigation.MainRoute
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.navigation.mainSection
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.navigation.navigateToSessionClient
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.navigation.sessionClientScreen
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.navigation.navigateToSetlistControls
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.navigation.navigateToSetlistEditor
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.navigation.setlistControlsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.navigation.setlistEditorScreen
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.navigation.navigateToSettings
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.navigation.settingsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.navigation.navigateToSongControls
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.navigation.navigateToSongEditor
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.navigation.songControlsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.navigation.songEditorScreen
import dev.thomas_kiljanczyk.lyriccast.tutorial.onboardingScreen
import dev.thomas_kiljanczyk.lyriccast.ui.shared.menu.gms_nearby_session.dialog.StartSessionServerDialog

/**
 * Top-level navigation graph. Navigation is organized using feature-based modules
 * following the Now in Android architecture patterns.
 *
 * @param appState Application state containing navigation controller and app-level state
 * @param activity Component activity reference for screens that require it
 * @param onOnboardingComplete called once the first-run onboarding carousel finishes
 * @param onOnboardingSkip called if the user skips the first-run onboarding carousel
 */
@Composable
fun LyricCastNavHost(
    appState: LyricCastAppState,
    activity: ComponentActivity,
    modifier: Modifier = Modifier,
    startDestination: Any = MainRoute,
    onOnboardingComplete: () -> Unit = {},
    onOnboardingSkip: () -> Unit = {},
) {
    val navController = appState.navController

    // The nearby "start session" dialog is triggered from the main screen's overflow menu, but
    // it's hosted here rather than as its own destination: it's a transient dialog, not a place
    // to navigate back to.
    var showStartSessionDialog by remember { mutableStateOf(false) }

    if (showStartSessionDialog) {
        StartSessionServerDialog(
            onDismiss = { showStartSessionDialog = false },
            onSessionStarted = { showStartSessionDialog = false },
            onShowPermissionDialog = { showStartSessionDialog = false }
        )
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        onboardingScreen(
            onComplete = onOnboardingComplete,
            onSkip = onOnboardingSkip
        )

        mainSection(
            activity = activity,
            onNavigateToSettings = navController::navigateToSettings,
            onNavigateToSongEditor = navController::navigateToSongEditor,
            onNavigateToSetlistEditor = navController::navigateToSetlistEditor,
            onNavigateToCategoryManager = navController::navigateToCategoryManager,
            onNavigateToSessionClient = navController::navigateToSessionClient,
            onNavigateToSongControls = navController::navigateToSongControls,
            onNavigateToSetlistControls = navController::navigateToSetlistControls,
            onStartSessionServer = { showStartSessionDialog = true },
        )

        settingsScreen(
            onNavigateUp = navController::popBackStack,
        )

        songEditorScreen(
            onNavigateUp = navController::popBackStack,
        )

        songControlsScreen(
            onNavigateUp = navController::popBackStack,
            onNavigateToSettings = navController::navigateToSettings,
        )

        setlistEditorScreen(
            onNavigateBack = navController::popBackStack,
        )

        setlistControlsScreen(
            onNavigateUp = navController::popBackStack,
            onNavigateToSettings = navController::navigateToSettings,
        )

        categoryManagerScreen(
            onNavigateUp = navController::popBackStack,
        )

        sessionClientScreen(
            onNavigateUp = navController::popBackStack,
        )
    }
}

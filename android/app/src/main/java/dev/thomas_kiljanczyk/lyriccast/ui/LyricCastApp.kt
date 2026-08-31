package dev.thomas_kiljanczyk.lyriccast.ui

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.settings.SettingsRepository
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ThemeOption
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourHost
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.rememberTourAnchorRegistry
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.navigation.MainRoute
import dev.thomas_kiljanczyk.lyriccast.navigation.LyricCastNavHost
import dev.thomas_kiljanczyk.lyriccast.navigation.rememberLyricCastAppState
import dev.thomas_kiljanczyk.lyriccast.tutorial.OnboardingRoute
import dev.thomas_kiljanczyk.lyriccast.tutorial.TutorialPhase
import dev.thomas_kiljanczyk.lyriccast.tutorial.TutorialViewModel
import dev.thomas_kiljanczyk.lyriccast.tutorial.cardPositionFor
import dev.thomas_kiljanczyk.lyriccast.tutorial.drawsSpotlightFor
import dev.thomas_kiljanczyk.lyriccast.tutorial.expandableFor
import dev.thomas_kiljanczyk.lyriccast.tutorial.rememberTourSteps
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun LyricCastApp(
    activity: ComponentActivity,
    settingsRepository: SettingsRepository,
    warmIntents: SharedFlow<Intent>,
    modifier: Modifier = Modifier,
) {
    val appTheme by settingsRepository.appTheme.collectAsStateWithLifecycle(initialValue = null)
    val themeOption = appTheme?.let { ThemeOption.fromValue(it) }

    val tutorialViewModel: TutorialViewModel = hiltViewModel(viewModelStoreOwner = activity)

    // Avoid drawing before the persisted theme is known, otherwise the first frame renders in the
    // wrong theme and flashes once DataStore delivers the real value.
    ReportDrawnWhen { appTheme != null }

    LyricCastTheme(themeOption = themeOption) {
        if (tutorialViewModel.phase == TutorialPhase.LOADING) {
            Surface(modifier.fillMaxSize()) { }
        } else {
            TutorialAwareNavHost(
                activity = activity,
                tutorialViewModel = tutorialViewModel,
                warmIntents = warmIntents,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun TutorialAwareNavHost(
    activity: ComponentActivity,
    tutorialViewModel: TutorialViewModel,
    warmIntents: SharedFlow<Intent>,
    modifier: Modifier = Modifier
) {
    val appState = rememberLyricCastAppState()
    val navController = appState.navController

    // The NavHost's own `startDestination` is fixed at composition time, so this only needs to be
    // correct the moment the graph is allowed to appear.
    val startDestination: Any = if (tutorialViewModel.phase == TutorialPhase.CAROUSEL) {
        OnboardingRoute
    } else {
        MainRoute
    }

    // "Show tutorial again" flips the phase on an already-composed graph, which the fixed
    // start destination cannot express.
    val startedOnCarousel = remember { tutorialViewModel.phase == TutorialPhase.CAROUSEL }
    LaunchedEffect(tutorialViewModel.phase) {
        if (tutorialViewModel.phase == TutorialPhase.CAROUSEL && !startedOnCarousel) {
            navController.navigate(OnboardingRoute) {
                popUpTo(MainRoute) { inclusive = false }
            }
        }
    }

    // Navigation resolves a cold-start lyriccast:// deep link on its own via the manifest
    // intent-filter; a warm intent delivered to an already-running Activity has to be replayed
    // into the existing NavController by hand.
    LaunchedEffect(navController) {
        warmIntents.collect { intent -> navController.handleDeepLink(intent) }
    }

    val steps = rememberTourSteps(appState)
    val isTouring = tutorialViewModel.phase == TutorialPhase.TOUR
    val currentStep = steps.getOrNull(tutorialViewModel.stepIndex).takeIf { isTouring }

    // The tour ends on Settings,
    // so finishing has to unwind the screens it navigated through.
    val endTutorial: () -> Unit = {
        tutorialViewModel.finish()
        navController.popBackStack(MainRoute, inclusive = false)
    }

    val advanceTutorial: () -> Unit = {
        if (tutorialViewModel.stepIndex >= steps.size - 1) {
            endTutorial()
        } else {
            tutorialViewModel.nextStep(steps.size)
        }
    }

    // On becoming current rather than while drawing,
    // so the anchor gets a layout pass to publish its bounds before the overlay looks.
    LaunchedEffect(currentStep) {
        currentStep?.onEnter?.invoke()
    }

    BackHandler(enabled = isTouring, onBack = endTutorial)

    val registry = rememberTourAnchorRegistry()
    val forcedExpandable = expandableFor(currentStep?.anchor)

    TourHost(
        registry = registry,
        step = currentStep,
        stepIndex = tutorialViewModel.stepIndex,
        stepCount = steps.size,
        onNext = advanceTutorial,
        onSkip = endTutorial,
        onAnchorMissing = { advanceTutorial() },
        modifier = modifier,
        expansion = { it == forcedExpandable },
        cardPosition = cardPositionFor(forcedExpandable),
        drawSpotlight = drawsSpotlightFor(currentStep?.anchor)
    ) {
        LyricCastNavHost(
            appState = appState,
            activity = activity,
            modifier = Modifier.fillMaxSize(),
            startDestination = startDestination,
            onOnboardingComplete = {
                tutorialViewModel.startTour()
                navController.navigate(MainRoute) {
                    popUpTo(OnboardingRoute) { inclusive = true }
                }
            },
            onOnboardingSkip = {
                tutorialViewModel.finish()
                navController.navigate(MainRoute) {
                    popUpTo(OnboardingRoute) { inclusive = true }
                }
            }
        )
    }
}

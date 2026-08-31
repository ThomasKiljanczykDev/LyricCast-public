package dev.thomas_kiljanczyk.lyriccast.tutorial

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.OnboardingCarousel
import kotlinx.serialization.Serializable

/**
 * Start destination on the first run.
 *
 * Unlike the tour overlay, the carousel can be a real destination, because it replaces the screen
 * rather than drawing over it.
 */
@Serializable
object OnboardingRoute

const val ONBOARDING_TRANSITION_MILLIS = 300

fun NavGraphBuilder.onboardingScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    composable<OnboardingRoute>(
        exitTransition = { fadeOut(tween(ONBOARDING_TRANSITION_MILLIS)) }
    ) {
        OnboardingCarousel(onComplete = onComplete, onSkip = onSkip)
    }
}

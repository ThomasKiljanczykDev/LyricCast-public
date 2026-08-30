/*
 * Created by Tomasz Kiljanczyk on 7/28/26, 6:42 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 7/28/26, 6:25 PM
 */

package dev.thomas_kiljanczyk.lyriccast.tutorial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.settings.SettingsRepository
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.CURRENT_ONBOARDING_VERSION
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.OnboardingOutcome
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.decideOnboarding
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class TutorialPhase {
    /** The gating decision has not landed yet, so the navigation graph must not compose. */
    LOADING,
    CAROUSEL,
    TOUR,
    NONE
}

/**
 * Owns tutorial progress for the lifetime of the Activity.
 *
 * Progress is deliberately not persisted, so a process death restarts the tutorial rather than
 * restoring a navigation position.
 */
@HiltViewModel
class TutorialViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val songsRepository: SongsRepository,
    private val setlistsRepository: SetlistsRepository
) : ViewModel() {

    var phase by mutableStateOf(TutorialPhase.LOADING)
        private set

    var stepIndex by mutableIntStateOf(0)
        private set

    init {
        viewModelScope.launch {
            val hasContent = songsRepository.getAllSongs().first().isNotEmpty() ||
                setlistsRepository.getAllSetlists().first().isNotEmpty()

            settingsRepository.onboardingCompletedVersion.collect { completedVersion ->
                if (phase == TutorialPhase.CAROUSEL || phase == TutorialPhase.TOUR) {
                    return@collect
                }

                phase = when (decideOnboarding(completedVersion, hasContent)) {
                    OnboardingOutcome.RUN -> {
                        stepIndex = 0
                        TutorialPhase.CAROUSEL
                    }

                    OnboardingOutcome.SKIP_AND_RECORD -> {
                        recordCompleted()
                        TutorialPhase.NONE
                    }

                    OnboardingOutcome.ALREADY_DONE -> TutorialPhase.NONE
                }
            }
        }
    }

    fun startTour() {
        stepIndex = 0
        phase = TutorialPhase.TOUR
    }

    fun nextStep(stepCount: Int) {
        if (stepIndex >= stepCount - 1) {
            finish()
        } else {
            stepIndex++
        }
    }

    /** Ends the tutorial for good. Also used for back during the tour. */
    fun finish() {
        phase = TutorialPhase.NONE
        viewModelScope.launch { recordCompleted() }
    }

    private suspend fun recordCompleted() {
        settingsRepository.updateOnboardingCompletedVersion(CURRENT_ONBOARDING_VERSION)
    }
}

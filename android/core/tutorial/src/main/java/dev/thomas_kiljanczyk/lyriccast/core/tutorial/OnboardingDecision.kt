/*
 * Created by Tomasz Kiljanczyk on 7/28/26, 6:42 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 7/28/26, 5:26 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.tutorial

/** Bump only when the tutorial changes enough that existing users should see it again. */
const val CURRENT_ONBOARDING_VERSION = 1

/**
 * Written by "Show tutorial again".
 *
 * Distinct from 0,
 * which means "never onboarded" and is subject to the has-content suppression below.
 * An explicit replay must run regardless of how full the library is,
 * or the setting does nothing for the people who tap it.
 */
const val REPLAY_REQUESTED_VERSION = -1

enum class OnboardingOutcome {
    RUN,

    /**
     * The upgrade path:
     * proto3 defaults mean installs predating the field read 0,
     * so without this an existing user would get an introduction they never asked for.
     */
    SKIP_AND_RECORD,

    ALREADY_DONE
}

/**
 * Pure so the upgrade rule is unit-testable without DataStore or Room.
 *
 * @param hasExistingContent whether any songs or setlists exist.
 *   An empty library is what distinguishes a new user from an upgrading one,
 *   and a long-time user with nothing saved is exactly who the tutorial helps.
 */
fun decideOnboarding(
    completedVersion: Int,
    hasExistingContent: Boolean
): OnboardingOutcome = when {
    completedVersion == REPLAY_REQUESTED_VERSION -> OnboardingOutcome.RUN
    completedVersion >= CURRENT_ONBOARDING_VERSION -> OnboardingOutcome.ALREADY_DONE
    hasExistingContent -> OnboardingOutcome.SKIP_AND_RECORD
    else -> OnboardingOutcome.RUN
}

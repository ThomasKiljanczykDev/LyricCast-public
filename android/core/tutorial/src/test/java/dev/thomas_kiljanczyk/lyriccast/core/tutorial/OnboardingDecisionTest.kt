/*
 * Created by Tomasz Kiljanczyk on 7/28/26, 6:42 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 7/28/26, 5:17 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OnboardingDecisionTest {

    @Test
    fun `fresh install with no content runs onboarding`() {
        val outcome = decideOnboarding(completedVersion = 0, hasExistingContent = false)

        assertThat(outcome).isEqualTo(OnboardingOutcome.RUN)
    }

    @Test
    fun `existing user upgrading is not interrupted`() {
        val outcome = decideOnboarding(completedVersion = 0, hasExistingContent = true)

        assertThat(outcome).isEqualTo(OnboardingOutcome.SKIP_AND_RECORD)
    }

    @Test
    fun `already onboarded at current version does nothing`() {
        val outcome = decideOnboarding(
            completedVersion = CURRENT_ONBOARDING_VERSION,
            hasExistingContent = false
        )

        assertThat(outcome).isEqualTo(OnboardingOutcome.ALREADY_DONE)
    }

    @Test
    fun `version beyond the current one is still treated as done`() {
        val outcome = decideOnboarding(
            completedVersion = CURRENT_ONBOARDING_VERSION + 5,
            hasExistingContent = true
        )

        assertThat(outcome).isEqualTo(OnboardingOutcome.ALREADY_DONE)
    }

    @Test
    fun `an explicit replay request runs even with a full library`() {
        val outcome = decideOnboarding(
            completedVersion = REPLAY_REQUESTED_VERSION,
            hasExistingContent = true
        )

        assertThat(outcome).isEqualTo(OnboardingOutcome.RUN)
    }
}

/*
 * Created by Tomasz Kiljanczyk on 7/28/26, 6:42 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 7/28/26, 5:26 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * One card in the intro carousel.
 *
 * Themed Material icons rather than bespoke artwork:
 * they inherit dynamic colour and dark mode for free and scale to any window size.
 */
@Immutable
data class OnboardingSlide(
    val icon: ImageVector,
    @param:StringRes val titleRes: Int,
    @param:StringRes val bodyRes: Int
)

/**
 * The intro deck.
 * Casting and sessions get separate slides because users routinely conflate the two.
 */
val onboardingSlides: ImmutableList<OnboardingSlide> = persistentListOf(
    OnboardingSlide(
        icon = Icons.Rounded.Tv,
        titleRes = R.string.onboarding_slide_welcome_title,
        bodyRes = R.string.onboarding_slide_welcome_body
    ),
    OnboardingSlide(
        icon = Icons.Rounded.MusicNote,
        titleRes = R.string.onboarding_slide_library_title,
        bodyRes = R.string.onboarding_slide_library_body
    ),
    OnboardingSlide(
        icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
        titleRes = R.string.onboarding_slide_setlists_title,
        bodyRes = R.string.onboarding_slide_setlists_body
    ),
    OnboardingSlide(
        icon = Icons.Rounded.Cast,
        titleRes = R.string.onboarding_slide_cast_title,
        bodyRes = R.string.onboarding_slide_cast_body
    ),
    OnboardingSlide(
        icon = Icons.Rounded.Devices,
        titleRes = R.string.onboarding_slide_sessions_title,
        bodyRes = R.string.onboarding_slide_sessions_body
    ),
    OnboardingSlide(
        icon = Icons.Rounded.Slideshow,
        titleRes = R.string.onboarding_slide_presenting_title,
        bodyRes = R.string.onboarding_slide_presenting_body
    )
)

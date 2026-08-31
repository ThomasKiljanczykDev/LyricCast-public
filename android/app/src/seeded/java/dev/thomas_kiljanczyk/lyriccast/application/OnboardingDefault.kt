package dev.thomas_kiljanczyk.lyriccast.application

import dev.thomas_kiljanczyk.lyriccast.core.tutorial.CURRENT_ONBOARDING_VERSION

/**
 * Seeding lands after the onboarding gate has read the library as empty,
 * so a profiled or benchmarked run would record the carousel instead of the app.
 */
internal const val DEFAULT_ONBOARDING_COMPLETED_VERSION = CURRENT_ONBOARDING_VERSION

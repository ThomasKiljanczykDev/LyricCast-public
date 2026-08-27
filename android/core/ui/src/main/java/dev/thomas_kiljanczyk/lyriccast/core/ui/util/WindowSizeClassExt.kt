/*
 * Created by Tomasz Kiljanczyk on 6/3/26, 11:22 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 6/3/26, 11:10 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.BREAKPOINTS_V1
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.computeWindowSizeClass

/**
 * The current window's [WindowSizeClass], derived from [LocalConfiguration].
 *
 * Prefer this over both `currentWindowAdaptiveInfo().windowSizeClass` AND
 * `LocalWindowInfo.current.containerSize`. `MainActivity` declares
 * `configChanges="orientation|screenSize"`, so the Activity is not recreated on rotation, and
 * under that setup both alternatives report the *pre-rotation* size while the configuration
 * change is in flight: `WindowMetricsCalculator` (used by `currentWindowAdaptiveInfo`) is
 * queried synchronously before the resize lands, and `LocalWindowInfo.containerSize` lags a
 * measure pass — both leave every adaptive layout one rotation behind (verified on device).
 * [LocalConfiguration]'s `screenWidthDp` / `screenHeightDp` come from the new `Configuration`
 * delivered the instant the change happens, so the size class tracks every rotation correctly.
 *
 * The `ConfigurationScreenWidthHeight` lint recommends `LocalWindowInfo.containerSize`, but
 * that is exactly the stale source under self-handled config changes — hence the suppression.
 */
@Suppress("ConfigurationScreenWidthHeight")
@Composable
fun currentWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    return BREAKPOINTS_V1.computeWindowSizeClass(
        widthDp = configuration.screenWidthDp.toFloat(),
        heightDp = configuration.screenHeightDp.toFloat()
    )
}

/**
 * Whether the window is at least the Expanded width breakpoint (840dp) wide, i.e. wide
 * enough to place primary content side-by-side (two-pane / horizontal layouts).
 *
 * Pure helper over [WindowSizeClass] so it stays unit-testable and can be combined with
 * other adaptive signals (height breakpoints, posture) at each call site, rather than
 * collapsing the full adaptive info into a single coarse boolean.
 */
fun WindowSizeClass.isWidthExpanded(): Boolean =
    isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

/**
 * Whether the window is shorter than the Medium height breakpoint (480dp), i.e. too short
 * to comfortably stack primary content vertically — e.g. a phone in landscape.
 *
 * Pure helper over [WindowSizeClass] so it stays unit-testable and can be combined with
 * other adaptive signals (width breakpoints, posture) at each call site, rather than
 * collapsing the full adaptive info into a single coarse boolean.
 */
fun WindowSizeClass.isHeightCompact(): Boolean =
    !isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)

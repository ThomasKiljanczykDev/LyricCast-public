/*
 * Created by Tomasz Kiljanczyk on 7/28/26, 6:42 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 7/28/26, 5:26 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

/**
 * One stop in the guided tour.
 *
 * [onEnter] is called when the step becomes current.
 * `:app` uses it to navigate.
 */
@Immutable
data class TourStep(
    val id: String,
    @param:StringRes val titleRes: Int,
    @param:StringRes val bodyRes: Int,
    val anchor: TourAnchor? = null,
    /**
     * When [anchor] publishes no bounds:
     * `false` drops the step,
     * `true` keeps it as a centered card with no spotlight.
     *
     * Use `true` where absence is expected but the message still matters:
     * the Cast button is hidden until a receiver is discovered.
     */
    val keepWhenAnchorMissing: Boolean = false,
    val onEnter: () -> Unit = {}
)

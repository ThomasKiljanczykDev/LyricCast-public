/*
 * Created by Tomasz Kiljanczyk on 8/30/25, 1:37 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 8/30/25, 12:04 AM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings

enum class ControlButtonHeightOption(val value: Int) {
    SMALL(88),
    MEDIUM(104),
    LARGE(128);

    companion object {
        fun fromValue(value: Int): ControlButtonHeightOption? = entries.find { it.value == value }
        val DEFAULT = SMALL
    }
}

/*
 * Created by Tomasz Kiljanczyk on 8/17/25, 10:35 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 8/17/25, 8:42 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.model.settings

enum class ThemeOption(val value: Int) {
    LIGHT(1),
    DARK(2),
    SYSTEM(-1);

    companion object {
        fun fromValue(value: Int): ThemeOption? = entries.find { it.value == value }
    }
}

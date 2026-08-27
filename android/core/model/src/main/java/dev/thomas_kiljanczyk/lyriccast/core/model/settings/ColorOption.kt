/*
 * Created by Tomasz Kiljanczyk on 8/17/25, 10:35 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 8/17/25, 8:42 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.model.settings

enum class ColorOption(val value: String) {
    BLACK("Black"),
    WHITE("White"),
    BLUE("Blue"),
    RED("Red"),
    DEEP_PINK("DeepPink");

    companion object {
        fun fromValue(value: String): ColorOption? = entries.find { it.value == value }
    }
}

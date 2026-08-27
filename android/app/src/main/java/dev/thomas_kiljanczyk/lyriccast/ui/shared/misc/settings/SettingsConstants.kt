/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 5:21 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 5:18 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings

import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ColorOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ThemeOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

object SettingsConstants {
    val themeOptions: ImmutableList<Pair<ThemeOption, UiText>> = listOf(
        ThemeOption.LIGHT to UiText.StringResource(R.string.preference_theme_light),
        ThemeOption.DARK to UiText.StringResource(R.string.preference_theme_dark),
        ThemeOption.SYSTEM to UiText.StringResource(R.string.preference_theme_system)
    ).toImmutableList()

    val buttonHeightOptions: ImmutableList<Pair<ControlButtonHeightOption, UiText>> = listOf(
        ControlButtonHeightOption.SMALL to UiText.StringResource(R.string.preference_controls_buttons_height_small),
        ControlButtonHeightOption.MEDIUM to UiText.StringResource(R.string.preference_controls_buttons_height_medium),
        ControlButtonHeightOption.LARGE to UiText.StringResource(R.string.preference_controls_buttons_height_large)
    ).toImmutableList()

    val colorOptions: ImmutableList<Pair<ColorOption, UiText>> = listOf(
        ColorOption.BLACK to UiText.StringResource(R.string.preference_color_black),
        ColorOption.WHITE to UiText.StringResource(R.string.preference_color_white),
        ColorOption.BLUE to UiText.StringResource(R.string.preference_color_blue),
        ColorOption.RED to UiText.StringResource(R.string.preference_color_red),
        ColorOption.DEEP_PINK to UiText.StringResource(R.string.preference_color_pink)
    ).toImmutableList()
}

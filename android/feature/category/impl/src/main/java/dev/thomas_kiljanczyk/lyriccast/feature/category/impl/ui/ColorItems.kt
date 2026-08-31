
package dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui

import dev.thomas_kiljanczyk.lyriccast.core.designsystem.color.BaseColors
import dev.thomas_kiljanczyk.lyriccast.core.model.ColorItem
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// TODO: Tweak the colors so they don't require different text colors when text is above them.
// Deferred to the Material3 color-token rework (app-style pass).
@Suppress("ForbiddenComment")
val colorItems: ImmutableList<ColorItem> = persistentListOf(
    ColorItem(
        UiText.StringResource(R.string.category_color_maroon),
        BaseColors.Maroon
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_tomato),
        BaseColors.Tomato
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_golden),
        BaseColors.GoldenRod
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_olive),
        BaseColors.OliveDrab
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_cyan),
        BaseColors.DarkCyan
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_turquoise),
        BaseColors.Turquoise
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_royal_blue),
        BaseColors.RoyalBlue
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_violet),
        BaseColors.BlueViolet
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_pink),
        BaseColors.DeepPink
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_navajo_white),
        BaseColors.NavajoWhite
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_slate_gray),
        BaseColors.SlateGray
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_gray),
        BaseColors.Gray
    ),
    ColorItem(
        UiText.StringResource(R.string.category_color_steel_blue),
        BaseColors.LightSteelBlue
    )
)

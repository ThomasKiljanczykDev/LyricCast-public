
package dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain

import android.content.res.Resources
import android.os.Build
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ColorOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ThemeOption
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

object SettingsConstants {
    const val SHARED_PREFERENCES_KEY = "appPrefs"
    const val PREFERENCE_LANGUAGE_KEY = "appLanguage"

    /** Picker entries from `locale_config.xml`, each labelled with its autonym. */
    fun languageOptions(resources: Resources): ImmutableList<Pair<LanguageOption, UiText>> =
        buildList {
            // System default option is only available on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(LanguageOption.SYSTEM to UiText.StringResource(R.string.preference_language_system))
            }
            SupportedLanguages.localeTags(resources).forEach { tag ->
                add(LanguageOption(tag) to UiText.DynamicString(SupportedLanguages.autonym(tag)))
            }
        }.toImmutableList()

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

package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ColorOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ThemeOption
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.R
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.LanguageOption
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.ui.MutableSettingsState
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.ui.SettingsScreen
import kotlinx.collections.immutable.toImmutableList

/** `LyricCast-settings-1.png`. */
class SettingsScreenshotTest {
    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun Settings() {
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                val state = remember {
                    MutableSettingsState().apply {
                        // Without an options list the row has no label to resolve the value
                        // against and falls back to `LanguageOption.toString()`.
                        language = LanguageOption.SYSTEM
                        languageOptions = listOf(
                            LanguageOption.SYSTEM to
                                UiText.StringResource(R.string.preference_language_system),
                            LanguageOption("en") to UiText.DynamicString("English"),
                            LanguageOption("pl") to UiText.DynamicString("Polski")
                        ).toImmutableList()
                        theme = ThemeOption.SYSTEM
                        themeOptions = listOf(
                            ThemeOption.SYSTEM to
                                UiText.StringResource(R.string.preference_theme_system),
                            ThemeOption.LIGHT to
                                UiText.StringResource(R.string.preference_theme_light),
                            ThemeOption.DARK to
                                UiText.StringResource(R.string.preference_theme_dark)
                        ).toImmutableList()
                        buttonHeight = ControlButtonHeightOption.SMALL
                        buttonHeightOptions = listOf(
                            ControlButtonHeightOption.SMALL to
                                UiText.StringResource(
                                    R.string.preference_controls_buttons_height_small
                                ),
                            ControlButtonHeightOption.MEDIUM to
                                UiText.StringResource(
                                    R.string.preference_controls_buttons_height_medium
                                ),
                            ControlButtonHeightOption.LARGE to
                                UiText.StringResource(
                                    R.string.preference_controls_buttons_height_large
                                )
                        ).toImmutableList()
                        isBlankEnabled = false
                        backgroundColor = ColorOption.BLACK
                        fontColor = ColorOption.WHITE
                        colorOptions = listOf(
                            ColorOption.RED to UiText.StringResource(R.string.preference_color_red),
                            ColorOption.WHITE to
                                UiText.StringResource(R.string.preference_color_white),
                            ColorOption.BLUE to UiText.StringResource(R.string.preference_color_blue)
                        ).toImmutableList()
                        maxFontSize = 90
                        isLoading = false
                    }
                }

                SettingsScreen(
                    state = state,
                    onNavigateUp = {},
                    onLanguageChange = {},
                    onThemeChange = {},
                    onButtonHeightChange = {},
                    onBlankEnabledChange = {},
                    onBackgroundColorChange = {},
                    onFontColorChange = {},
                    onMaxFontSizeChange = {},
                    onReplayTutorial = {}
                )
            }
        }
    }
}

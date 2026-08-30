/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:28 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:27 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ColorOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ThemeOption
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.tourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.Loading
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.R
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.LanguageOption
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DONATE_URL = "https://buymeacoffee.com/thomas.kiljanczyk.dev"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(onNavigateUp: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.settings_title)) },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_up)
                )
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun PreviewSettingsTopBar() {
    LyricCastTheme {
        SettingsTopBar(onNavigateUp = {})
    }
}

@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val scope = rememberCoroutineScope()
    var showLoading by remember { mutableStateOf(state.isLoading) }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            showLoading = false
            return@LaunchedEffect
        }

        showLoading = false
        delay(500.milliseconds)
        showLoading = true
    }

    Crossfade(state.isLoading) { loading ->
        if (loading) {
            AnimatedVisibility(
                visible = showLoading,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
                exit = fadeOut()
            ) {
                Loading(Modifier.fillMaxSize())
            }
        } else {
            SettingsScreen(
                state = state,
                onNavigateUp = onNavigateUp,
                onLanguageChange = { viewModel.updateLanguage(it) },
                onThemeChange = { scope.launch { viewModel.updateTheme(it) } },
                onButtonHeightChange = { scope.launch { viewModel.updateButtonHeight(it) } },
                onBlankEnabledChange = { scope.launch { viewModel.updateBlankEnabled(it) } },
                onBackgroundColorChange = { scope.launch { viewModel.updateBackgroundColor(it) } },
                onFontColorChange = { scope.launch { viewModel.updateFontColor(it) } },
                onMaxFontSizeChange = { scope.launch { viewModel.updateMaxFontSize(it) } },
                onReplayTutorial = { scope.launch { viewModel.replayTutorial() } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onNavigateUp: () -> Unit,
    onLanguageChange: (LanguageOption) -> Unit,
    onThemeChange: (ThemeOption) -> Unit,
    onButtonHeightChange: (ControlButtonHeightOption?) -> Unit,
    onBlankEnabledChange: (Boolean) -> Unit,
    onBackgroundColorChange: (ColorOption?) -> Unit,
    onFontColorChange: (ColorOption?) -> Unit,
    onMaxFontSizeChange: (Int) -> Unit,
    onReplayTutorial: () -> Unit
) {
    Scaffold(
        topBar = {
            SettingsTopBar(onNavigateUp = onNavigateUp)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // App Settings Section
            SettingsCategory(title = stringResource(R.string.preference_section_app), content = {
                SettingsCardGroup {
                    item {
                        SettingsRowWithDialog(
                            title = stringResource(R.string.preference_language_title),
                            value = state.language,
                            options = state.languageOptions,
                            onValueChange = onLanguageChange
                        )
                    }
                    item {
                        SettingsRowWithDialog(
                            title = stringResource(R.string.preference_theme_title),
                            value = state.theme,
                            options = state.themeOptions,
                            onValueChange = onThemeChange
                        )
                    }
                    item {
                        SettingsRowWithDialog(
                            title = stringResource(R.string.preference_controls_button_height_title),
                            value = state.buttonHeight,
                            options = state.buttonHeightOptions,
                            onValueChange = onButtonHeightChange
                        )
                    }
                    item {
                        SettingsRowButton(
                            title = stringResource(R.string.preference_show_tutorial_title),
                            subtitle = stringResource(R.string.preference_show_tutorial_summary),
                            onClick = onReplayTutorial
                        )
                    }
                    item {
                        val context = LocalContext.current
                        SettingsRowButton(
                            title = stringResource(R.string.preference_donate_title),
                            subtitle = stringResource(R.string.preference_donate_summary),
                            icon = ImageVector.vectorResource(R.drawable.coffee),
                            onClick = {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, DONATE_URL.toUri())
                                )
                            },
                            modifier = Modifier.tourAnchor(TourAnchor.SETTINGS_DONATE)
                        )
                    }
                }
            })

            // Chromecast Settings Section
            SettingsCategory(
                title = stringResource(R.string.preference_section_chromecast),
                modifier = Modifier.tourAnchor(TourAnchor.SETTINGS_APPEARANCE),
                content = {
                    SettingsCardGroup {
                        item {
                            SettingsCheckbox(
                                title = stringResource(R.string.preference_blank_title),
                                checked = state.isBlankEnabled,
                                onCheckedChange = onBlankEnabledChange,
                                modifier = Modifier.tourAnchor(TourAnchor.SETTINGS_BLANK_ON_START)
                            )
                        }
                        item {
                            SettingsRowWithDialog(
                                title = stringResource(R.string.preference_cast_background_title),
                                value = state.backgroundColor,
                                options = state.colorOptions,
                                onValueChange = onBackgroundColorChange
                            )
                        }
                        item {
                            SettingsRowWithDialog(
                                title = stringResource(R.string.preference_cast_font_color_title),
                                value = state.fontColor,
                                options = state.colorOptions,
                                onValueChange = onFontColorChange
                            )
                        }
                        item {
                            SettingsSlider(
                                title = stringResource(R.string.preference_cast_max_font_size_title),
                                value = state.maxFontSize.toFloat(),
                                valueRange = 30f..100f,
                                onValueChange = { onMaxFontSizeChange(it.toInt()) }
                            )
                        }
                    }
                })
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettingsScreen() {
    LyricCastTheme {
        val state = remember {
            MutableSettingsState().apply {
                language = LanguageOption.SYSTEM
                languageOptions = listOf(
                    LanguageOption.SYSTEM to UiText.StringResource(R.string.preference_language_system),
                    LanguageOption("en") to UiText.DynamicString("English"),
                    LanguageOption("pl") to UiText.DynamicString("Polski")
                ).toImmutableList()
                theme = ThemeOption.SYSTEM
                themeOptions = listOf(
                    ThemeOption.SYSTEM to UiText.StringResource(R.string.preference_theme_system),
                    ThemeOption.LIGHT to UiText.StringResource(R.string.preference_theme_light),
                    ThemeOption.DARK to UiText.StringResource(R.string.preference_theme_dark)
                ).toImmutableList()
                buttonHeight = ControlButtonHeightOption.SMALL
                buttonHeightOptions = listOf(
                    ControlButtonHeightOption.SMALL to
                        UiText.StringResource(R.string.preference_controls_buttons_height_small),
                    ControlButtonHeightOption.MEDIUM to
                        UiText.StringResource(R.string.preference_controls_buttons_height_medium),
                    ControlButtonHeightOption.LARGE to
                        UiText.StringResource(R.string.preference_controls_buttons_height_large)
                ).toImmutableList()
                isBlankEnabled = true
                backgroundColor = ColorOption.BLACK
                fontColor = ColorOption.WHITE
                colorOptions = listOf(
                    ColorOption.RED to UiText.StringResource(R.string.preference_color_red),
                    ColorOption.WHITE to UiText.StringResource(R.string.preference_color_white),
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

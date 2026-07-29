/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 5:21 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 5:21 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.data.SettingsRepository
import dev.thomas_kiljanczyk.lyriccast.domain.models.UiText
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings.ColorOption
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings.SettingsConstants
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings.ThemeOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SettingsState {
    val theme: ThemeOption
    val themeOptions: ImmutableList<Pair<ThemeOption, UiText>>
    val buttonHeight: ControlButtonHeightOption?
    val buttonHeightOptions: ImmutableList<Pair<ControlButtonHeightOption, UiText>>
    val isBlankEnabled: Boolean
    val backgroundColor: ColorOption?
    val fontColor: ColorOption?
    val colorOptions: ImmutableList<Pair<ColorOption, UiText>>
    val maxFontSize: Int
    val isLoading: Boolean
}

class MutableSettingsState : SettingsState {
    override var theme by mutableStateOf(ThemeOption.SYSTEM)
    override var themeOptions by mutableStateOf<ImmutableList<Pair<ThemeOption, UiText>>>(
        persistentListOf()
    )
    override var buttonHeight by mutableStateOf<ControlButtonHeightOption?>(null)
    override var buttonHeightOptions by mutableStateOf<ImmutableList<Pair<ControlButtonHeightOption, UiText>>>(
        persistentListOf()
    )
    override var isBlankEnabled by mutableStateOf(false)
    override var backgroundColor by mutableStateOf<ColorOption?>(null)
    override var fontColor by mutableStateOf<ColorOption?>(null)
    override var colorOptions by mutableStateOf<ImmutableList<Pair<ColorOption, UiText>>>(
        persistentListOf()
    )
    override var maxFontSize by mutableIntStateOf(90)
    override var isLoading by mutableStateOf(false)
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableSettingsState().apply { isLoading = true }
    val state: SettingsState = _state

    init {
        viewModelScope.launch {
            settingsRepository.getAllSettings().collect { settings ->
                _state.apply {
                    theme = ThemeOption.fromValue(settings.appTheme) ?: ThemeOption.SYSTEM
                    themeOptions = SettingsConstants.themeOptions
                    buttonHeight =
                        ControlButtonHeightOption.fromValue(settings.controlButtonsHeight)
                    buttonHeightOptions = SettingsConstants.buttonHeightOptions
                    isBlankEnabled = settings.blankOnStart
                    backgroundColor = ColorOption.fromValue(settings.backgroundColor)
                    fontColor = ColorOption.fromValue(settings.fontColor)
                    colorOptions = SettingsConstants.colorOptions
                    maxFontSize = settings.maxFontSize
                    isLoading = false
                }
            }
        }
    }

    suspend fun updateTheme(theme: ThemeOption) {
        withContext(Dispatchers.IO) {
            settingsRepository.updateTheme(theme)
        }
    }

    suspend fun updateButtonHeight(height: ControlButtonHeightOption?) {
        height?.let {
            withContext(Dispatchers.IO) {
                settingsRepository.updateButtonHeight(it)
            }
        }
    }

    suspend fun updateBlankEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            settingsRepository.updateBlankEnabled(enabled)
        }
    }

    suspend fun updateBackgroundColor(color: ColorOption?) {
        color?.let {
            withContext(Dispatchers.IO) {
                settingsRepository.updateBackgroundColor(it)
            }
        }
    }

    suspend fun updateFontColor(color: ColorOption?) {
        color?.let {
            withContext(Dispatchers.IO) {
                settingsRepository.updateFontColor(it)
            }
        }
    }

    suspend fun updateMaxFontSize(size: Int) {
        withContext(Dispatchers.IO) {
            settingsRepository.updateMaxFontSize(size)
        }
    }
}

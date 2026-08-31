
package dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.settings.SettingsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ColorOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ThemeOption
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.REPLAY_REQUESTED_VERSION
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.LanguageOption
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.LocaleManager
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.SettingsConstants
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

interface SettingsState {
    val language: LanguageOption
    val languageOptions: ImmutableList<Pair<LanguageOption, UiText>>
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
    override var language by mutableStateOf(LanguageOption.SYSTEM)
    override var languageOptions by mutableStateOf<ImmutableList<Pair<LanguageOption, UiText>>>(
        persistentListOf()
    )
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
    @param:ApplicationContext
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val localeManager: LocaleManager
) : ViewModel() {

    private val _state = MutableSettingsState().apply { isLoading = true }
    val state: SettingsState = _state

    init {
        viewModelScope.launch {
            settingsRepository.getAllSettings().collect { settings ->
                _state.apply {
                    language = localeManager.getSavedLanguage()
                    languageOptions = SettingsConstants.languageOptions(context.resources)
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

    fun updateLanguage(language: LanguageOption) {
        _state.language = language
        localeManager.updateLanguage(language)
    }

    suspend fun updateTheme(theme: ThemeOption) {
        settingsRepository.updateTheme(theme)
    }

    suspend fun updateButtonHeight(height: ControlButtonHeightOption?) {
        height?.let {
            settingsRepository.updateButtonHeight(it)
        }
    }

    suspend fun updateBlankEnabled(enabled: Boolean) {
        settingsRepository.updateBlankEnabled(enabled)
    }

    suspend fun updateBackgroundColor(color: ColorOption?) {
        color?.let {
            settingsRepository.updateBackgroundColor(it)
        }
    }

    suspend fun updateFontColor(color: ColorOption?) {
        color?.let {
            settingsRepository.updateFontColor(it)
        }
    }

    suspend fun updateMaxFontSize(size: Int) {
        settingsRepository.updateMaxFontSize(size)
    }

    /**
     * Runs the carousel and tour again on next start.
     * Otherwise the tutorial is unreachable after one dismissal.
     */
    suspend fun replayTutorial() {
        settingsRepository.updateOnboardingCompletedVersion(REPLAY_REQUESTED_VERSION)
    }
}

package dev.thomas_kiljanczyk.lyriccast.data

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.thomas_kiljanczyk.lyriccast.common.di.ApplicationScope
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.LanguageOption
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.LocaleManager as LocaleManagerInterface
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.SettingsConstants
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.SupportedLanguages
import dev.thomas_kiljanczyk.lyriccast.shared.misc.allowingThreadDiskReads
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * [LocaleManagerInterface] implementation backed by [AppCompatDelegate]'s per-app language APIs on
 * API 33+, and by SharedPreferences (applied through the same API on process start) below that.
 */
@Singleton
class LocaleManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
    @param:Dispatcher(LyricCastDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : LocaleManagerInterface {

    override fun getSavedLanguage(): LanguageOption {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // API 27-32 only. The first read of this file is a real open + XML parse, and it can't
            // move off the main thread: the result feeds AppCompatDelegate.setApplicationLocales,
            // which has to be applied before the first Activity resolves its resources or the UI
            // renders a frame in the wrong locale.
            val languageTag = allowingThreadDiskReads {
                val prefs = context.getSharedPreferences(
                    SettingsConstants.SHARED_PREFERENCES_KEY,
                    Context.MODE_PRIVATE
                )
                prefs.getString(SettingsConstants.PREFERENCE_LANGUAGE_KEY, null)
            }
            LanguageOption.fromLocaleTag(
                languageTag,
                SupportedLanguages.localeTags(context.resources)
            )
        } else {
            val applied = AppCompatDelegate.getApplicationLocales()
            if (applied.isEmpty) {
                LanguageOption.SYSTEM
            } else {
                LanguageOption(
                    SupportedLanguages.matchTag(
                        applied.toLanguageTags().substringBefore(','),
                        context.resources
                    )
                )
            }
        }
    }

    override fun updateLanguage(language: LanguageOption) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            applicationScope.launch(ioDispatcher) {
                AppCompatDelegate.setApplicationLocales(language.toLocaleListCompat())
            }
            return
        }

        val prefs = context.getSharedPreferences(
            SettingsConstants.SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )
        prefs.edit { putString(SettingsConstants.PREFERENCE_LANGUAGE_KEY, language.localeTag) }

        AppCompatDelegate.setApplicationLocales(language.toLocaleListCompat())
    }

    /**
     * Applies the saved language preference on app startup. Should be called from
     * Application.onCreate().
     */
    fun applyLocaleOnStartup() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            var savedLanguage = getSavedLanguage()

            if (savedLanguage.localeTag == null) {
                savedLanguage = SupportedLanguages.resolveForDevice(context)
                updateLanguage(savedLanguage)
            }

            AppCompatDelegate.setApplicationLocales(savedLanguage.toLocaleListCompat())
        }
    }
}

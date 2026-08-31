
package dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain

/**
 * The implementation should be provided by the app module.
 */
interface LocaleManager {
    fun getSavedLanguage(): LanguageOption

    fun updateLanguage(language: LanguageOption)
}

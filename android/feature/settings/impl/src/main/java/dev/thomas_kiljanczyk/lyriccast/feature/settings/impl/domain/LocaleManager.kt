/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 4:15 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 1/29/26, 3:55 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain

/**
 * Interface for managing application locale/language settings.
 * The implementation should be provided by the app module.
 */
interface LocaleManager {
    fun getSavedLanguage(): LanguageOption

    fun updateLanguage(language: LanguageOption)
}

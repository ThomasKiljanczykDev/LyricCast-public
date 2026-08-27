/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 4:15 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/6/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain

import androidx.core.os.LocaleListCompat

/**
 * One language-picker entry, by BCP-47 tag; null is "system default", offered on Android 13+ only.
 * Not an enum — the shipped set lives in `locale_config.xml`, see [SupportedLanguages].
 */
@JvmInline
value class LanguageOption(val localeTag: String?) {

    fun toLocaleListCompat(): LocaleListCompat =
        if (localeTag == null) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(localeTag)
        }

    companion object {
        val SYSTEM = LanguageOption(null)

        /** Falls back to [SYSTEM] for a tag that is no longer shipped. */
        fun fromLocaleTag(tag: String?, supported: List<String>): LanguageOption =
            if (tag != null && tag in supported) LanguageOption(tag) else SYSTEM
    }
}

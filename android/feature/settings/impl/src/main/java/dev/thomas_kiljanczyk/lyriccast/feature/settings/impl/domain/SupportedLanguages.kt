/*
 * Created by Tomasz Kiljanczyk on 8/6/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/6/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain

import android.content.Context
import android.content.res.Resources
import dev.thomas_kiljanczyk.lyriccast.core.ui.R as CoreUiR
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.xmlpull.v1.XmlPullParser

/**
 * The shipped languages, read from `core/ui/res/xml/locale_config.xml` so the in-app picker
 * cannot drift from the system one.
 */
object SupportedLanguages {
    private const val ANDROID_NS = "http://schemas.android.com/apk/res/android"

    /** Language tags in `locale_config.xml` order — the order the picker shows them in. */
    fun localeTags(resources: Resources): ImmutableList<String> {
        val tags = mutableListOf<String>()
        resources.getXml(CoreUiR.xml.locale_config).use { parser ->
            var event = parser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG && parser.name == "locale") {
                    parser.getAttributeValue(ANDROID_NS, "name")?.let(tags::add)
                }
                event = parser.next()
            }
        }
        return tags.toImmutableList()
    }

    fun options(resources: Resources): ImmutableList<LanguageOption> =
        localeTags(resources).map { LanguageOption(it) }.toImmutableList()

    /** The language's own name, capitalised in *its* locale (`Locale.ROOT` breaks Turkish "i"). */
    fun autonym(localeTag: String): String {
        val locale = Locale.forLanguageTag(localeTag)
        return locale.getDisplayLanguage(locale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }

    /** Maps any tag onto a shipped one, so `en-US` selects `en`. Null when nothing matches. */
    fun matchTag(tag: String, resources: Resources): String? {
        val language = Locale.forLanguageTag(tag).language
        return localeTags(resources).firstOrNull { Locale.forLanguageTag(it).language == language }
    }

    /**
     * The starting option below API 33, where there is no per-app language setting and so no
     * SYSTEM option. Falls back to English, not to the first shipped language.
     */
    fun resolveForDevice(context: Context): LanguageOption {
        // Locale.getLanguage() returns legacy codes; "tl" is the predecessor of "fil".
        val deviceLanguage = when (val language = Locale.getDefault().language) {
            "tl" -> "fil"
            else -> language
        }
        val supported = localeTags(context.resources)
        val match = supported.firstOrNull { Locale.forLanguageTag(it).language == deviceLanguage }
        return LanguageOption(match ?: "en")
    }
}

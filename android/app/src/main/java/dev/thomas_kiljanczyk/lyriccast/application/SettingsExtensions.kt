package dev.thomas_kiljanczyk.lyriccast.application

import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings

fun AppSettings.Builder.setValue(key: String, value: Any?): AppSettings.Builder {
    val preferenceValue: String = value?.toString() ?: ""
    if (preferenceValue.isBlank()) {
        return this
    }

    when (key) {
        "appTheme" -> {
            val appThemeValue = preferenceValue.toInt()
            this.appTheme = appThemeValue
        }

        "controlsButtonHeight" -> {
            this.controlButtonsHeight = preferenceValue.toInt()
        }

        "blankedOnStart" -> {
            this.blankOnStart = preferenceValue.toBooleanStrict()
        }

        "backgroundColor" -> {
            this.backgroundColor = preferenceValue
        }

        "fontColor" -> {
            this.fontColor = preferenceValue
        }

        "fontMaxSize" -> {
            this.maxFontSize = preferenceValue.toInt()
        }
    }

    return this
}

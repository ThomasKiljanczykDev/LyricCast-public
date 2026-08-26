/*
 * Created by Tomasz Kiljanczyk on 8/30/25, 1:37 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 8/30/25, 12:07 AM
 */

package dev.thomas_kiljanczyk.lyriccast.application

fun AppSettings.getCastConfiguration(): CastConfiguration {
    return CastConfiguration(
        this.backgroundColor,
        this.fontColor,
        this.maxFontSize
    )
}

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

/*
 * Created by Tomasz Kiljanczyk on 8/30/25, 1:37 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 8/30/25, 12:07 AM
 */

package dev.thomas_kiljanczyk.lyriccast.application

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ColorOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings
import java.io.InputStream
import java.io.OutputStream

internal const val DEFAULT_MAX_FONT_SIZE = 90

object AppSettingsSerializer : Serializer<AppSettings> {
    override val defaultValue: AppSettings = AppSettings.newBuilder()
        .setOnboardingCompletedVersion(DEFAULT_ONBOARDING_COMPLETED_VERSION)
        .build()

    override suspend fun readFrom(input: InputStream): AppSettings {
        try {
            val settingsBuilder = AppSettings.parseFrom(input).toBuilder()
            setDefaultValues(settingsBuilder)

            return settingsBuilder.build()
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: AppSettings,
        output: OutputStream
    ) {
        t.writeTo(output)
    }

    private fun setDefaultValues(settingsBuilder: AppSettings.Builder) {
        if (settingsBuilder.appTheme == 0) {
            settingsBuilder.appTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        if (settingsBuilder.controlButtonsHeight == 0) {
            settingsBuilder.controlButtonsHeight = ControlButtonHeightOption.DEFAULT.value
        }
        if (settingsBuilder.backgroundColor.isBlank()) {
            settingsBuilder.backgroundColor = ColorOption.BLACK.value
        }
        if (settingsBuilder.fontColor.isBlank()) {
            settingsBuilder.fontColor = ColorOption.WHITE.value
        }
        if (settingsBuilder.maxFontSize == 0) {
            settingsBuilder.maxFontSize = DEFAULT_MAX_FONT_SIZE
        }
        // Covers a store written before the field existed.
        // The replay sentinel is negative, so it survives.
        if (settingsBuilder.onboardingCompletedVersion == 0) {
            settingsBuilder.onboardingCompletedVersion = DEFAULT_ONBOARDING_COMPLETED_VERSION
        }
    }
}

val Context.settingsDataStore: DataStore<AppSettings> by dataStore(
    fileName = "settings.proto",
    serializer = AppSettingsSerializer
)

/*
 * Created by Tomasz Kiljanczyk on 8/17/25, 11:14 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 8/17/25, 10:50 PM
 */

package dev.thomas_kiljanczyk.lyriccast.data

import androidx.datastore.core.DataStore
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ColorOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ThemeOption
import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<AppSettings>
) {
    fun getAllSettings(): Flow<AppSettings> = dataStore.data

    suspend fun updateTheme(theme: ThemeOption) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setAppTheme(theme.value)
                .build()
        }
    }

    suspend fun updateButtonHeight(height: ControlButtonHeightOption) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setControlButtonsHeight(height.value)
                .build()
        }
    }

    suspend fun updateBlankEnabled(enabled: Boolean) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setBlankOnStart(enabled)
                .build()
        }
    }

    suspend fun updateBackgroundColor(color: ColorOption) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setBackgroundColor(color.value)
                .build()
        }
    }

    suspend fun updateFontColor(color: ColorOption) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setFontColor(color.value)
                .build()
        }
    }

    suspend fun updateMaxFontSize(size: Int) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setMaxFontSize(size)
                .build()
        }
    }
}

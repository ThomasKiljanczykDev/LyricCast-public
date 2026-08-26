/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 12:35 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 12:35 AM
 */

package dev.thomas_kiljanczyk.lyriccast.application

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import com.google.android.gms.cast.framework.CastContext
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import dev.thomas_kiljanczyk.lyriccast.shared.cast.CastMessagingContext
import dev.thomas_kiljanczyk.lyriccast.shared.cast.CastSessionListener
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings.ControlButtonHeightOption
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltAndroidApp
class LyricCastApplication : Application() {

    companion object {
        val PERMISSIONS = preparePermissionArray()

        private fun preparePermissionArray(): Array<String> {
            val result = mutableListOf(
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE
            )

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                result.add(Manifest.permission.BLUETOOTH)
                result.add(Manifest.permission.BLUETOOTH_ADMIN)
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                result.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                result.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                result.add(Manifest.permission.BLUETOOTH_ADVERTISE)
                result.add(Manifest.permission.BLUETOOTH_CONNECT)
                result.add(Manifest.permission.BLUETOOTH_SCAN)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.add(Manifest.permission.NEARBY_WIFI_DEVICES)
            }

            return result.toTypedArray()
        }
    }

    @Inject
    lateinit var dataStore: DataStore<AppSettings>

    @Inject
    lateinit var castMessagingContext: CastMessagingContext

    @Inject
    lateinit var castContext: CastContext

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()

        // Initializes CastContext
        castContext.sessionManager.addSessionManagerListener(CastSessionListener(onStarted = {
            CoroutineScope(Dispatchers.Default).launch {
                val blankOnStart = dataStore.data.first().blankOnStart
                castMessagingContext.sendBlank(blankOnStart)
            }
        }, onEnded = { castMessagingContext.onSessionEnded() }))

        DynamicColors.applyToActivitiesIfAvailable(this)

        // Initialize default values in DataStore
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.updateData { currentSettings ->
                if (currentSettings == AppSettings.getDefaultInstance()) {
                    AppSettings.newBuilder()
                        .setAppTheme(-1) // System default
                        .setControlButtonsHeight(ControlButtonHeightOption.DEFAULT.value)
                        .setBlankOnStart(false)
                        .setBackgroundColor("Black")
                        .setFontColor("White")
                        .setMaxFontSize(DEFAULT_MAX_FONT_SIZE)
                        .build()
                } else {
                    currentSettings
                }
            }
        }

        dataStore.data.onEach {
            var appTheme: Int? = it.appTheme
            appTheme = if (appTheme == 0) null else appTheme
            if (appTheme != null) {
                AppCompatDelegate.setDefaultNightMode(appTheme)
            }
        }.launchIn(CoroutineScope(Dispatchers.Main))

        val isDebuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        if (isDebuggable) {
            setupStrictMode()
        }
    }

    private fun setupStrictMode() {
        val threadPolicy =
            StrictMode.ThreadPolicy.Builder().detectAll().permitCustomSlowCalls().penaltyLog()
                .penaltyDialog().build()

        StrictMode.setThreadPolicy(threadPolicy)

        val vmPolicy =
            StrictMode.VmPolicy.Builder().detectActivityLeaks().detectFileUriExposure().penaltyLog()
                .build()

        StrictMode.setVmPolicy(vmPolicy)
    }
}

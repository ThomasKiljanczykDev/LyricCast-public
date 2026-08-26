/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 8:03 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 7:36 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.cast.framework.CastContext
import dagger.hilt.android.AndroidEntryPoint
import dev.thomas_kiljanczyk.lyriccast.data.SettingsRepository
import dev.thomas_kiljanczyk.lyriccast.ui.LyricCastApp
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var castContext: CastContext

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            LyricCastApp(
                activity = this,
                settingsRepository = settingsRepository
            )
        }
    }
}

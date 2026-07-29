/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 6:36 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 6:32 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.shared.components

import android.view.ContextThemeWrapper
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.ui.shared.menu.cast.CustomMediaRouteDialogFactory

@Composable
fun CastButton(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    val context = LocalContext.current

    val castButton = remember {
        MediaRouteButton(
            ContextThemeWrapper(
                context,
                R.style.Theme_LyricCast_CastButton
            )
        ).apply {
            CastButtonFactory.setUpMediaRouteButton(context.applicationContext, this)
            dialogFactory = CustomMediaRouteDialogFactory()
        }
    }

    AndroidView(
        factory = { castButton },
        update = { },
        modifier = modifier.size(size)
    )
}
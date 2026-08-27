/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 6:36 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 6:32 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.cast.ui

import android.view.ContextThemeWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import dev.thomas_kiljanczyk.lyriccast.core.cast.R

@Composable
fun CastButton(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    // `AndroidView` wraps a real `MediaRouteButton` that requires Google Play Services / a
    // CastContext to initialize. Neither is available in Compose Preview or Screenshot Testing's
    // layoutlib sandbox, and the interop View subtree it creates also crashes the tooling's
    // composition-data walker there -- so render a static placeholder instead when inspected.
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Rounded.Cast,
                contentDescription = null,
                modifier = Modifier.size(size / 2)
            )
        }
        return
    }

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

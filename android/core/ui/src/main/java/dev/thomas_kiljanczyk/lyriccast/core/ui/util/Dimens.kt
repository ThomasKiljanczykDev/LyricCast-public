/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 AM
 */

package dev.thomas_kiljanczyk.lyriccast.core.ui.util

import androidx.compose.ui.unit.dp

/**
 * Bottom `contentPadding` letting a list's last item scroll clear of the FAB: 56dp FAB + 16dp gap +
 * 16dp margin. The main screen overlays its FAB rather than using `Scaffold`'s FAB slot, so
 * nothing reserves this space.
 */
val FabListBottomSpacing = 88.dp

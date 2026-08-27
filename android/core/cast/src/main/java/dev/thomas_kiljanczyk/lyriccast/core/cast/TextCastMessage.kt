/*
 * Created by Tomasz Kiljanczyk on 25/01/2025, 18:55
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 25/01/2025, 18:55
 */

package dev.thomas_kiljanczyk.lyriccast.core.cast

import kotlinx.serialization.Serializable

@Serializable
data class TextCastMessage(
    val text: String
)

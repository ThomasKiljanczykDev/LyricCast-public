/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:27 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datatransfer.models

import kotlinx.serialization.Serializable

@Serializable
data class SetlistDto(
    val name: String,
    val songs: List<String>
)
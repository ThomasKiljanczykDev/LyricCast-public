/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:42 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:32 PM
 */

package dev.thomas_kiljanczyk.lyriccast.common.extensions

import org.apache.commons.lang3.StringUtils

/**
 * Normalizes a string by removing any accents or diacritical marks.
 *
 * @return A normalized string with accents or diacritical marks removed.
 */
fun String.normalize(): String {
    return StringUtils.stripAccents(this)
}

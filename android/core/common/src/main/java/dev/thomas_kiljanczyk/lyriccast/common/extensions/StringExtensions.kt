package dev.thomas_kiljanczyk.lyriccast.common.extensions

import org.apache.commons.lang3.StringUtils

fun String.normalize(): String {
    return StringUtils.stripAccents(this)
}

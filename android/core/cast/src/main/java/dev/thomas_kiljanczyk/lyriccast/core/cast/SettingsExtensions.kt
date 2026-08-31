package dev.thomas_kiljanczyk.lyriccast.core.cast

import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings

fun AppSettings.getCastConfiguration(): CastConfiguration {
    return CastConfiguration(
        this.backgroundColor,
        this.fontColor,
        this.maxFontSize
    )
}

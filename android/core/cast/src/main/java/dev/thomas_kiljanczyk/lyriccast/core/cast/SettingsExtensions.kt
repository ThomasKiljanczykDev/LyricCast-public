/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 11:44 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 1/29/26, 11:33 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.cast

import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings

fun AppSettings.getCastConfiguration(): CastConfiguration {
    return CastConfiguration(
        this.backgroundColor,
        this.fontColor,
        this.maxFontSize
    )
}

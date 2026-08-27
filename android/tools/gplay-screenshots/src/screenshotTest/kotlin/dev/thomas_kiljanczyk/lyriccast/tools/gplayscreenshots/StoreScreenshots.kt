/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 AM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.gplayscreenshots

import androidx.compose.ui.tooling.preview.Preview

/**
 * Phone (9:16) in every shipped language, plus a single representative tablet (16:9) render.
 *
 * Trimmed from the private client's full phone x tablet x locale matrix: this module exists to
 * get the screenshot-test compile step green and give the next commit (README screenshot
 * regeneration) a working pipeline, not to ship every device/locale combination. A tablet render
 * per screen (English only) is kept so both aspect ratios stay exercised; add the rest of the
 * tablet locales if/when the README actually needs them.
 *
 * `locale` is a resource qualifier, not a BCP-47 tag; `name` is the filename token the compositor
 * matches on. Keep the phone set in step with `core/ui/res/xml/locale_config.xml`.
 */
@Preview(name = "phone-am", device = DEVICE_SPEC, locale = "am")
@Preview(name = "phone-de", device = DEVICE_SPEC, locale = "de")
@Preview(name = "phone-en", device = DEVICE_SPEC, locale = "en")
@Preview(name = "phone-es", device = DEVICE_SPEC, locale = "es")
@Preview(name = "phone-fil", device = DEVICE_SPEC, locale = "b+fil")
@Preview(name = "phone-fr", device = DEVICE_SPEC, locale = "fr")
@Preview(name = "phone-in", device = DEVICE_SPEC, locale = "in")
@Preview(name = "phone-it", device = DEVICE_SPEC, locale = "it")
@Preview(name = "phone-ko", device = DEVICE_SPEC, locale = "ko")
@Preview(name = "phone-pl", device = DEVICE_SPEC, locale = "pl")
@Preview(name = "phone-pt", device = DEVICE_SPEC, locale = "pt")
@Preview(name = "phone-sw", device = DEVICE_SPEC, locale = "sw")
@Preview(name = "phone-vi", device = DEVICE_SPEC, locale = "vi")
@Preview(name = "phone-zh", device = DEVICE_SPEC, locale = "b+zh+Hans")
@Preview(name = "tablet-en", device = TABLET_SPEC, locale = "en")
annotation class StoreScreenshots

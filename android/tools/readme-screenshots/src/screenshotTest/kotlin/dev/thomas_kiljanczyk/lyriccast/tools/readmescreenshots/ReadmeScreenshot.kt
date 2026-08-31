package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.ui.tooling.preview.Preview

/**
 * Phone, 1080x2400 px -- the geometry the images already in `android/docs/images/` were captured
 * at, so a refreshed render drops straight into the README at the same `height="640"`.
 */
const val DEVICE_SPEC = "spec:width=1080px,height=2400px,dpi=420"

/**
 * One render per shot: English, dark theme, phone. The README is a single-language document and
 * every screenshot in it is dark. Pair with `@PreviewTest`.
 */
@Preview(name = "phone", device = DEVICE_SPEC, locale = "en")
annotation class ReadmeScreenshot

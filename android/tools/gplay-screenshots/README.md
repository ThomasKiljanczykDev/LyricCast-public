# Play Store screenshot renderer

Renders real app screens (phone + a representative tablet render, across the app's shipped
languages) for use as Play Store / README screenshots. All code lives in `src/screenshotTest`;
nothing ships in an APK.

Unlike the private reference this was ported from, there is no compositing/publishing stage here
-- that lived in a private CDK/store-listing pipeline that is intentionally not part of this
repo. This module's rendered PNGs are consumed directly (e.g. for the top-level `README.md`).

## Render

```sh
cd android && ./gradlew :tools:gplay-screenshots:updateDebugScreenshotTest
```

One JUnit class per screen under `src/screenshotTest/kotlin/.../gplayscreenshots/`, one
`@Composable` per shot, annotated `@PreviewTest @StoreScreenshots` (multipreview -> phone x every
shipped language, plus one tablet render).

| Class                    | Real composable                          |
|--------------------------|-------------------------------------------|
| `SongsScreenshotTest`    | `MainScreen` (songs tab)                  |
| `SetlistsScreenshotTest` | `SetlistEditorScreen`                     |
| `DisplayScreenshotTest`  | `SetlistControlsScreen`                   |
| `SettingsScreenshotTest` | `SettingsScreen`                          |
| `SessionScreenshotTest`  | `SessionClientScreen`                     |

Output: `src/screenshotTestDebug/reference/.../<TestClass>/<method>_<device>-<locale>_<hash>_0.png`.
Gitignored -- fully reproducible from source, and nothing in this repo runs
`validateDebugScreenshotTest` as a regression gate, so there's no reason to check them in. Re-run
this command whenever a screen or its demo data changes.

### Layoutlib workarounds

- **No window insets** -- `ScreenshotSurface.kt` adds 24dp bottom padding to fake gesture-nav.
- **No Play Services** -- screens that would otherwise draw a live Cast button render its static
  fallback under `LocalInspectionMode`.

### Scope

This module intentionally covers a smaller surface than the private reference it was ported from:

- No chord-editor, soft-delete, or crop/scan screenshots -- those features aren't in this app.
- One tablet locale (English) instead of the full tablet x locale matrix, to keep this module a
  quick, representative compile check rather than a full asset pipeline. Extend
  `StoreScreenshots.kt` if more tablet locales are ever needed.

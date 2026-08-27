# README screenshot renderer

Renders the screenshots embedded in [`android/README.md`](../../README.md) from the real Compose
screens, so a UI change can be reflected in the docs without anyone opening an emulator. All code
lives in `src/screenshotTest`; nothing ships in an APK.

This app is not published to any store, so there is no listing-graphics pipeline here -- the
README is the only consumer of these renders. They are English, phone-sized and dark, one render
per shot.

Demo data comes from
[`ScreenshotData`](../../core/ui/src/main/java/dev/thomas_kiljanczyk/lyriccast/core/ui/preview/ScreenshotData.kt)
in `core:ui`, which is where it has to live -- a `screenshotTest` source set cannot be depended on
from another module. Titles and category names are string resources, so a render at another locale
translates the *data* and not just the UI chrome; lyrics stay as `PreviewData.sampleLyrics`, which
is Lorem ipsum and so language-neutral. It is deliberately not the rest of `PreviewData`: those
fixtures carry placeholder rows and uncategorised songs, which are fine in the IDE and wrong in a
README.

## Render

```sh
cd android && ./gradlew :tools:readme-screenshots:updateDebugScreenshotTest
```

Output lands in `src/screenshotTestDebug/reference/.../<TestClass>/<method>_phone_<hash>_0.png`,
which is gitignored -- it is reproducible from source and nothing validates it as a baseline.

## Copy into the docs

Deliberately manual, and rare: this runs when a screen changes, not on every build. Copy each
render over its counterpart in [`android/docs/images/`](../../docs/images):

| Test class / method                       | `docs/images/`               |
|-------------------------------------------|------------------------------|
| `CategoriesScreenshotTest.CategoryList`   | `LyricCast-categories-1.png` |
| `CategoriesScreenshotTest.CategoryEditor` | `LyricCast-categories-2.png` |
| `SongsScreenshotTest.SongList`            | `LyricCast-songs-1.png`      |
| `SongsScreenshotTest.SongEditor`          | `LyricCast-songs-2.png`      |
| `SetlistsScreenshotTest.SetlistList`      | `LyricCast-setlists-1.png`   |
| `SetlistsScreenshotTest.SetlistEditor`    | `LyricCast-setlists-2.png`   |
| `ImportScreenshotTest.ImportDialog`       | `LyricCast-import-1.png`     |
| `CastScreenshotTest.SongControls`         | `LyricCast-cast-1.png`       |
| `CastScreenshotTest.SetlistControls`      | `LyricCast-cast-2.png`       |
| `SessionScreenshotTest.ChooseSession`     | `LyricCast-session-1.png`    |
| `SessionScreenshotTest.SessionClient`     | `LyricCast-session-2.png`    |
| `SettingsScreenshotTest.Settings`         | `LyricCast-settings-1.png`   |

`LyricCast-splash.png` is not rendered here -- it is a hand-made banner, not a screen.

## Layoutlib workarounds

- **No window insets.** Layoutlib never dispatches them, so a full-bleed screen renders flush
  against the canvas edge and its bottom corners look clipped.
  [`ScreenshotSurface`](src/screenshotTest/kotlin/dev/thomas_kiljanczyk/lyriccast/tools/readmescreenshots/ScreenshotSurface.kt)
  fakes the gesture-navigation inset back in.
- **No Hilt.** Every screen is rendered through its state-driven overload. `MainScreen` takes its
  two tab bodies as slots for exactly this reason -- their defaults reach for the Hilt-backed
  screens.
- **No Play Services.** Screens that would draw a live Cast button render its static fallback
  under `LocalInspectionMode`.
- **Dialogs render, but not as dialogs.** Layoutlib draws a `Dialog` inline rather than in its own
  window, which loses both the scrim over the screen behind it and the side margins
  `usePlatformDefaultWidth` would impose -- the dialog ends up edge-to-edge on a bare background.
  [`DialogScreenshot`](src/screenshotTest/kotlin/dev/thomas_kiljanczyk/lyriccast/tools/readmescreenshots/DialogScreenshot.kt)
  puts both back. The margin cannot come from the wrapper's own layout, because the inline `Dialog`
  escapes its parent's constraints; it has to be passed to the dialog composable as a `Modifier`,
  which is why every dialog rendered here takes one.

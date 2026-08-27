![LyricCast](docs/images/LyricCast-splash.png "LyricCast")

[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="70" title="Coming soon!" alt="Get it on Google Play. Coming soon!">](https://play.google.com/store/apps/details?id=dev.thomas_kiljanczyk.lyriccast)

[![CI android](https://github.com/ThomasKiljanczykDev/LyricCast-public/actions/workflows/ci-android.yml/badge.svg)](https://github.com/ThomasKiljanczykDev/LyricCast-public/actions/workflows/ci-android.yml)

# LyricCast Android Client

This is the Android client for the *LyricCast* app.

# Features

## Song lyrics database

*LyricCast* allows you to create an on-device database of song lyrics and setlists.
The app supports importing and exporting the full or parts of the database to and from a file.
Android Room is used as a database to store the data on-device.

### Categories

The app lets you create categories which are used to organize songs.
Category manager is accessible through the action menu on the main screen.
You can name the category and set a color for easier identification.
Category name must be unique.

### Songs

The app includes a song editor that allows you to create and edit songs.
The song editor allows you to set the song title, assign a category, and add lyrics.
Lyrics are split in sections which can be then shown on the screen one after another.
Song title must be unique.

<img src="docs/images/LyricCast-songs-1.png" alt="Songs - song list" height="640">

### Setlists

Songs can be grouped into a setlist.
A setlist can be created using the floating action button on the main screen or by selecting
multiple song and creating
an 'ad-hoc' setlist.
The setlist editor allows you to set the setlist name and add songs.
Songs can be reordered by dragging them using the handle on the right side.
You can also duplicate songs withing the setlist by long pressing the song and selecting 'Duplicate'
in the action menu.
Setlist name must be unique.

<img src="docs/images/LyricCast-setlists-2.png" alt="Setlists - setlist editor" height="640">

### Import/Export

To ease up the process of creating and managing the database, the app supports importing and
exporting the full or parts
of the database to and from a file.
The app supports its own JSON based file format
and [OpenSong](https://opensong.org/development/file-formats/) to allow
the users to import their existing databases.
You can instruct the app to replace your current database on import or replace song lyrics and
setlists on conflict.
The default import behavior is to ignore conflicts and import only new songs and setlists.

Files can be imported either through the in-app file picker, by opening a `.zip`/`.xml` export
directly from a file manager or another app's share sheet, or via a `lyriccast://` deep link -- in
every case the format is still confirmed explicitly in the import dialog before anything is
written to the database.

## Cast lyrics using Google Cast.

*LyricCast* allows you to cast lyrics to your TV screen using Google Cast.
The functionality is accessible through the ubiquitous Cast button in the app bar.
Song lyrics will be cast after selecting a songs or setlist on the main screen.
You can blank the screen from the song/setlist controls if need be.

The cast lyrics will be automatically resized to fit the screen with a maximum font size set in the
settings.

<img src="docs/images/LyricCast-cast-1.png" alt="Controls - the lyrics-display/control screen" height="640">

## Start a session and let others join in.

*LyricCast* allows you to start a session and let others join in.
When joining a session you will see the lyrics that are currently being cast by the session host.
The session feature is based
on [Nearby Connections API](https://developers.google.com/nearby/connections/overview).
A session host cannot join another session.

<img src="docs/images/LyricCast-session-1.png" alt="Session - client view" height="640">

## First-run onboarding

New installs are greeted with a short onboarding carousel introducing the app before landing on the
main screen.

## Settings

*LyricCast* allows you to customize the app through the settings screen.
The settings screen consists of multiple sections.

<img src="docs/images/LyricCast-settings-1.png" alt="Settings" height="640">

### Application

In this section you can change the following settings:

* Language - override the app's display language independently of the system setting. LyricCast
  ships English and Polish alongside 12 community-style locales: Amharic, French, German,
  Indonesian, Italian, Korean, Portuguese, Simplified Chinese, Swahili, Filipino, Spanish, and
  Vietnamese.
* Theme - choose between light, dark or system default theme.
* Controls button height - set the height of the controls buttons. This option is useful if you need
  to be able to
  quickly tap the buttons and don't miss.

### Chromecast

In this section you can change the following settings:

* Blanked on start - when casting lyrics, the screen will be initially blanked.
  The blank is applied after successfully connecting to the cast device.
* Background color - the background color of the cast lyrics screen.
* Font color - the font color of the cast lyrics screen.
* Maximum font size - the maximum font size of the cast lyrics screen.

# Development Environment

**LyricCast** uses the Gradle build system and can be imported directly into Android Studio
(make sure you are using the latest stable version
available [here](https://developer.android.com/studio)).

Change the run configuration to `app`.

![image](https://user-images.githubusercontent.com/873212/210559920-ef4a40c5-c8e0-478b-bb00-4879a8cf184a.png)

The `debug` and `release` build variants can be built and run.

![image](https://user-images.githubusercontent.com/873212/210560507-44045dc5-b6d5-41ca-9746-f0f7acf22f8e.png)

# Architecture

*LyricCast* is designed to work entirely on-device.
That being said any external library that requires internet connection might send data to the
internet (i.e. `Firebase`,
`Google Cast SDK`).

## Project structure

The app follows a Now-in-Android-style modularization: a thin `app` module wires together
independent `core` and `feature` modules through Hilt, with shared Gradle setup centralized in a
composite `build-logic` build.

This project consists of:

* `build-logic/convention` - shared Gradle convention plugins (Android library/application, Compose,
  Hilt, etc.) applied by every module instead of copy-pasted `build.gradle.kts` boilerplate
* `app` - the application shell: app-level DI wiring, the nav host that stitches feature routes
  together, and `MainActivity` (theme, deep-link/file-open intent consumption)
* `core/*` - foundation and cross-cutting modules with no feature-specific UI, e.g. `common`,
  `model`, `database` (Room), `data`, `domain`, `data-transfer` (LyricCast/OpenSong format
  converters), `designsystem`, `ui`, `datastore-proto`, `session`, `nearby`, `cast`, `playback`,
  `sync` (import/deep-link plumbing), `tutorial` (onboarding), plus `testing`/`data-test` and the
  `*-test` fixture modules used by the modules above
* `feature/*/impl` - one module per user-facing feature area (`category`, `main`, `session`,
  `setlist`, `settings`, `song`), each owning its own screens, ViewModels, and navigation routes
* `baselineprofile` - macrobenchmark module generating a baseline profile for the `app` module
* `tools/gplay-screenshots` - a Compose Screenshot Testing module that renders store-style
  screenshots from the app's real composables (see [Testing](#testing) below); it ships no code to
  users

## Architecture components

```mermaid
%%{init: {"flowchart": {"defaultRenderer": "elk"}} }%%
flowchart LR
    subgraph androidDevice1[Android Device]
        application1[LyricCast Application]
        roomDatabase[Room Database]
        application1 -->|Store| roomDatabase -->|Retrieve| application1
    end

    subgraph androidDevice2[Android Device]
        application2[LyricCast Application]
    end

    application2 -->|Join session| application1
    application1 -->|Cast| application2
    application1 -->|Cast| googleCastDevice[Google Cast Device]
```

# Build

The app contains the usual `debug` and `release` build variants, plus a `seededRelease` variant
used by the baseline-profile/macrobenchmark module.

For development use the `debug` variant. For UI performance testing use the `release` variant.

# Testing

To facilitate testing of components, **LyricCast** uses dependency injection with
[Hilt](https://developer.android.com/training/dependency-injection/hilt-android).

## Test Architecture

Most data layer components are defined as interfaces.
Then, concrete implementations (with various dependencies) are bound to provide those interfaces to
other components in the app.
In tests, **LyricCast** notably does _not_ use any mocking libraries.
Instead, the production implementations can be replaced with test doubles using Hilt's testing APIs
(or via manual constructor injection for `ViewModel` tests).

`core/testing` and `core/data-test` (plus the `core/nearby-test` and `core/cast-test` fixture
modules) hold the shared test doubles: fake repository implementations
(`SongsRepositoryFakeImpl`, `CategoriesRepositoryFakeImpl`, `SetlistsRepositoryFakeImpl`,
`DataTransferRepositoryFakeImpl`), a `TestDataModule`/`TestDispatchersModule` pair that swaps them
into the Hilt graph, and a `BaseComposeTest` base class for instrumented Compose UI tests.

## Compose Testing

**LyricCast** uses [Compose Testing](https://developer.android.com/jetpack/compose/testing) for UI
tests:

* **Compose Test Rule** - Uses `createAndroidComposeRule<MainActivity>()` for full app testing
* **Semantic tree testing** - Tests interact with UI elements using semantics rather than
  implementation details
* **Fake implementations** - Repository fakes back the database with an in-memory `MutableList`
  for predictable test data
* **Hilt test modules** - `TestDataModule` replaces production repositories

### Test Examples

* **FilterSongsComposeTest** / **FilterSetlistsComposeTest** - Test song/setlist filtering
* **NavigationTabComposeTest** - Tests tab navigation between Songs and Setlists
* **DeleteSongComposeTest** / **DeleteSetlistComposeTest** - Test deletion functionality

## Screenshot Testing

`tools/gplay-screenshots` uses [Compose Screenshot
Testing](https://developer.android.com/studio/preview/compose-screenshot-testing) to render a
handful of hero screens (Songs, Setlist editor, Settings, Session client, the lyrics-display/
controls screen) against the real production composables and `PreviewData` fixtures, in every
locale the app ships. It is where the images on this page come from; it contributes no code to the
shipped app.

## Test Structure

Unit tests live alongside the module they test (`core/*/src/test`, `feature/*/impl/src/test`).
Instrumented Compose UI tests live in `app/src/androidTest`. CI (`ci-android.yml`) runs
lint/detekt/unit tests on every push and pull request, plus an instrumented test matrix on Gradle
Managed Devices.

To run the tests go to the corresponding module and run the whole test directory or selected tests
using the IDE.

# UI

The app is built entirely with [Jetpack Compose](https://developer.android.com/jetpack/compose)
following [Material 3 guidelines](https://m3.material.io/). The declarative UI approach provides
better performance, maintainability, and follows modern Android development best practices.

## Architecture

**LyricCast** uses Jetpack Compose for all UI components:

* **Navigation Compose** - For screen navigation and deep linking between songs, setlists, and
  settings
* **Material 3 Components** - Cards, buttons, dropdowns, text fields, and floating action buttons
* **State management** - Reactive UI using `remember`, `mutableStateOf`, and `collectAsState` with
  ViewModels
* **Custom components** - Reusable Compose components for lyrics display, song lists, and category
  management
* **Theming** - Complete Material 3 theming system with dynamic color support

## Themes

The app supports Material 3 theming with two color schemes:

* **Dynamic color** - Uses colors based on
  the [user's current color theme](https://material.io/blog/announcing-material-you) (Android 12+)
* **Default theme** - Uses predefined Material 3 colors when dynamic color is not supported

Both themes support dark mode with proper contrast ratios and accessibility considerations. The
Compose theming system ensures consistent styling across all screens and components.

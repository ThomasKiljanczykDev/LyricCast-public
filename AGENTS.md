# AGENTS.md

Machine-facing summary for AI coding agents working in this repo.

## What this repo is

**LyricCast** — song-lyrics database + Google Cast presenter for Android. Product overview in
[`README.md`](README.md).

- `android/` — Kotlin / Jetpack Compose client. Hilt, Room, Proto DataStore, type-safe Navigation
  Compose. `app` + `core/*` + `feature/*`.

## Commands (`cd android/`)

| Command | Use |
| --- | --- |
| `./gradlew build` | Full build, all variants. Pre-push gate. |
| `./gradlew :app:assembleDebug` | Just the app debug APK. |
| `./gradlew test` | All JVM unit tests. |
| `./gradlew lint` | Android Lint, all modules. |
| `./gradlew detekt` | detekt + ktlint, all modules. Config in `android/config/detekt/detekt.yml`. |
| `./gradlew detekt -PdetektAutoCorrect=true` | Apply the ktlint fixes in place. |

## Conventions

- **Comments: default to none.** Add one only where the code needs clarification that a well-named
  identifier can't supply — a hidden constraint, a workaround, a non-obvious invariant. Never narrate
  what the code already says: no restating the identifier in prose, no section-header comments, no
  `@param`/`@property`/`@return` lines that just repeat the name, no class docs like "Represents a X".
  One line, attached to the code it describes. `detekt` enforces the length side of this
  (`comments-house > ExcessiveComment`, [`config/detekt/detekt.yml`](android/config/detekt/detekt.yml))
  and fails the build on comment blocks longer than 4 lines — that is a backstop, not the bar; most
  comments should not exist at all.
- Never write a comment that narrates the conversation that produced the change (e.g. "as requested",
  "per the user", "phase 2") — `style > ForbiddenComment` in the same config rejects these.
- **detekt is the Kotlin style gate**, with ktlint inside it — no standalone ktlint or Spotless. There
  is no baseline file, on purpose: tune the config or fix the source, never freeze a finding.

## Boundaries

**Never:**

- Disable a quality gate (`@Suppress`, `--no-verify`) without a one-line reason.
- Force-push to `main` or rewrite published history.

## Commits

[Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/), scope optional
(`feat(session):`).

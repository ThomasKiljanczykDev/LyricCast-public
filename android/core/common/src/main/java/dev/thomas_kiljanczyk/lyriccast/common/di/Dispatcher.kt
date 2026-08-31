package dev.thomas_kiljanczyk.lyriccast.common.di

import javax.inject.Qualifier

enum class LyricCastDispatchers {
    IO,
    Default,
    Main
}

/**
 * Qualifies an injected [kotlinx.coroutines.CoroutineDispatcher]. Production code never references
 * `Dispatchers.IO` / `Dispatchers.Default` directly, so tests can swap in a `TestDispatcher`.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: LyricCastDispatchers)

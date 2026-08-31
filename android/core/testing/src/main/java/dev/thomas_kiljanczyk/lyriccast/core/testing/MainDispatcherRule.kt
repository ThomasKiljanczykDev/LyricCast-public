package dev.thomas_kiljanczyk.lyriccast.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Puts [testDispatcher] behind `Dispatchers.Main` for the duration of a test.
 *
 * ViewModels keep launching on `viewModelScope`, which dispatches to `Main`; on the JVM there is
 * no main looper, so without this every `viewModelScope.launch` throws.
 */
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

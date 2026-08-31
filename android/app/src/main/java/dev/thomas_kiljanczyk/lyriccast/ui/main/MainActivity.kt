package dev.thomas_kiljanczyk.lyriccast.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.cast.framework.CastContext
import dagger.hilt.android.AndroidEntryPoint
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.settings.SettingsRepository
import dev.thomas_kiljanczyk.lyriccast.core.sync.PendingImportHolder
import dev.thomas_kiljanczyk.lyriccast.ui.LyricCastApp
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    @JvmField
    var castContext: CastContext? = null

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var pendingImportHolder: PendingImportHolder

    // Navigation handles the launch intent itself, but not one delivered to a running activity.
    // The NavController lives in composition, so warm intents are forwarded there instead.
    private val _warmIntents = MutableSharedFlow<Intent>(extraBufferCapacity = 1)
    private val warmIntents: SharedFlow<Intent> = _warmIntents.asSharedFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        consumeImportIntent(intent)

        setContent {
            LyricCastApp(
                activity = this,
                settingsRepository = settingsRepository,
                warmIntents = warmIntents
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Without this getIntent() keeps returning the intent the activity was launched with.
        setIntent(intent)

        consumeImportIntent(intent)
        _warmIntents.tryEmit(intent)
    }

    /**
     * Picks up a file handed to the app by a file manager or the share sheet.
     *
     * The intent is marked once handled, so a recreated activity does not re-offer the same file.
     * The marker rides on the intent rather than in storage; it is lost if the process is killed,
     * which re-offers the import - the same outcome as the holder itself being lost.
     */
    private fun consumeImportIntent(intent: Intent) {
        if (intent.getBooleanExtra(EXTRA_IMPORT_HANDLED, false)) {
            return
        }

        val uri: Uri? = when (intent.action) {
            Intent.ACTION_VIEW -> intent.data

            Intent.ACTION_SEND ->
                IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)

            else -> null
        }

        // A lyriccast:// deep link is an ACTION_VIEW too; Navigation handles those.
        if (uri == null || uri.scheme == DEEP_LINK_SCHEME) {
            return
        }

        intent.putExtra(EXTRA_IMPORT_HANDLED, true)
        lifecycleScope.launch { pendingImportHolder.offer(uri) }
    }

    private companion object {
        const val EXTRA_IMPORT_HANDLED = "dev.thomas_kiljanczyk.lyriccast.IMPORT_HANDLED"
        const val DEEP_LINK_SCHEME = "lyriccast"
    }
}

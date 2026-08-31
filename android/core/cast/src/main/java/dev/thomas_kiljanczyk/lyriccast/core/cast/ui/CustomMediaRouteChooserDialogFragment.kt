package dev.thomas_kiljanczyk.lyriccast.core.cast.ui

import android.content.Context
import android.os.Bundle
import androidx.mediarouter.app.MediaRouteChooserDialog
import androidx.mediarouter.app.MediaRouteChooserDialogFragment
import dev.thomas_kiljanczyk.lyriccast.core.cast.R

class CustomMediaRouteChooserDialogFragment : MediaRouteChooserDialogFragment() {
    override fun onCreateChooserDialog(
        context: Context,
        savedInstanceState: Bundle?
    ): MediaRouteChooserDialog {
        val dialog = super.onCreateChooserDialog(context, savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.media_route_dialog_background)

        return dialog
    }
}

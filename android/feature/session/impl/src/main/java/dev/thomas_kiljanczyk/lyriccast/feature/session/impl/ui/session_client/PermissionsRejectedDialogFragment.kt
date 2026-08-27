/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:55 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:54 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.R

class PermissionsRejectedDialogFragment(
    @param:StringRes
    @field:StringRes
    private val messageResId: Int? = null,
    private val onIgnore: () -> Unit = {},
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(messageResId ?: R.string.main_activity_missing_permissions)
            .setNegativeButton(R.string.ignore) { _, _ ->
                onIgnore()
            }
            .setPositiveButton(R.string.launch_activity_go_to_settings) { _, _ ->
                openAppSettings()
            }
            .create()
    }

    private fun openAppSettings() {
        val packageName = requireContext().packageName
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.setData(uri)

        startActivity(intent)
    }
}

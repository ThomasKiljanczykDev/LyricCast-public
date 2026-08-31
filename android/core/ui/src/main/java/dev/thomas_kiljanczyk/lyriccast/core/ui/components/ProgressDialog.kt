package dev.thomas_kiljanczyk.lyriccast.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.ui.R

@Composable
fun ProgressDialog(
    message: UiText,
    modifier: Modifier = Modifier,
    showOkButton: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = if (showOkButton) {
            onDismiss
        } else {
            {}
        },
        title = null,
        text = {
            ProgressDialogContent(message = message)
        },
        confirmButton = {
            ProgressDialogButton(showOkButton = showOkButton, onDismiss = onDismiss)
        },
        properties = DialogProperties(
            dismissOnBackPress = showOkButton,
            dismissOnClickOutside = showOkButton
        ),
        modifier = modifier
    )
}

@Composable
private fun ProgressDialogContent(message: UiText) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .widthIn(min = 280.dp)
            .padding(vertical = 8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = message.asString(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProgressDialogButton(
    showOkButton: Boolean,
    onDismiss: () -> Unit
) {
    if (showOkButton) {
        TextButton(onClick = onDismiss) {
            Text(stringResource(android.R.string.ok))
        }
    }
}

@Composable
fun ProgressDialog(
    messageRes: Int,
    modifier: Modifier = Modifier,
    showOkButton: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    ProgressDialog(
        message = UiText.StringResource(messageRes),
        showOkButton = showOkButton,
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
private fun ProgressDialogPreview() {
    LyricCastTheme {
        Surface {
            ProgressDialog(
                message = UiText.StringResource(R.string.main_activity_export_preparing_data),
                showOkButton = false
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ProgressDialogWithButtonPreview() {
    LyricCastTheme {
        Surface {
            ProgressDialog(
                message = UiText.StringResource(R.string.main_activity_import_successful),
                showOkButton = true,
                onDismiss = {}
            )
        }
    }
}

/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.rememberScreenshotData
import dev.thomas_kiljanczyk.lyriccast.datatransfer.enums.ImportFormat
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.import_dialog.ImportDialogStateless
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.import_dialog.MutableImportDialogState

/** `LyricCast-import-1.png`. */
class ImportScreenshotTest {
    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun ImportDialog() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                DialogScreenshot(
                    background = { MainScreenshot(data) },
                    dialog = {
                        ImportDialogStateless(
                            state = MutableImportDialogState().apply {
                                // OpenSong is the interesting choice here -- the README's import
                                // section is largely about bringing an existing OpenSong database
                                // across.
                                importFormat = ImportFormat.OPEN_SONG
                            },
                            onDismiss = {},
                            onImport = {},
                            onImportFormatChange = {},
                            onDeleteAllChange = {},
                            onReplaceOnConflictChange = {},
                            modifier = DialogInsetModifier
                        )
                    }
                )
            }
        }
    }
}

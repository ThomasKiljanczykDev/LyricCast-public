/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:52 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:49 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.common.io.UriStreamDataSource
import dev.thomas_kiljanczyk.lyriccast.core.model.ImportOptions
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.sync.ImportInput
import dev.thomas_kiljanczyk.lyriccast.core.sync.PendingImport
import dev.thomas_kiljanczyk.lyriccast.core.sync.PendingImportHolder
import dev.thomas_kiljanczyk.lyriccast.core.sync.use_case.ExportDataUseCase
import dev.thomas_kiljanczyk.lyriccast.core.sync.use_case.ImportDataUseCase
import dev.thomas_kiljanczyk.lyriccast.datatransfer.enums.ImportFormat
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.R
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface MainScreenState {
    val selectedTab: MainTab
    val isSessionServerRunning: Boolean
    val isExporting: Boolean
    val isImporting: Boolean
    val exportProgress: UiText?
    val importProgress: UiText?
    val showProgressDialog: Boolean
    val progressMessage: UiText?
    val progressCompleted: Boolean
}

class MutableMainScreenState : MainScreenState {
    override var selectedTab by mutableStateOf(MainTab.SONGS)
    override var isSessionServerRunning by mutableStateOf(false)
    override var isExporting by mutableStateOf(false)
    override var isImporting by mutableStateOf(false)
    override var exportProgress by mutableStateOf<UiText?>(null)
    override var importProgress by mutableStateOf<UiText?>(null)
    override var showProgressDialog by mutableStateOf(false)
    override var progressMessage by mutableStateOf<UiText?>(null)
    override var progressCompleted by mutableStateOf(false)
}

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val importDataUseCase: ImportDataUseCase,
    private val exportDataUseCase: ExportDataUseCase,
    private val pendingImportHolder: PendingImportHolder,
    private val uriStreams: UriStreamDataSource,
    payloadTransport: PayloadTransport
) : ViewModel() {
    val state: MainScreenState
        field = MutableMainScreenState()

    /** A file handed in from outside (e.g. via `ACTION_VIEW`/`ACTION_SEND`), if one is waiting. */
    val pendingImport: StateFlow<PendingImport?> = pendingImportHolder.pendingImport

    /** Stops offering the pending file once it has been imported, or the dialog was dismissed. */
    fun clearPendingImport() {
        pendingImportHolder.clear()
    }

    init {
        // Monitor session server status
        payloadTransport.serverIsRunning
            .onEach { state.isSessionServerRunning = it }
            .launchIn(viewModelScope)
    }

    fun selectTab(tab: MainTab) {
        state.selectedTab = tab
    }

    fun showProgressDialog(message: UiText) {
        state.progressMessage = message
        state.showProgressDialog = true
        state.progressCompleted = false
    }

    fun updateProgressMessage(message: UiText) {
        state.progressMessage = message
    }

    fun completeProgress() {
        state.progressCompleted = true
    }

    fun hideProgressDialog() {
        state.showProgressDialog = false
        state.progressMessage = null
        state.progressCompleted = false
    }

    suspend fun exportAll(destination: Uri) {
        state.isExporting = true
        try {
            val cacheDir = uriStreams.cacheDirPath()
            uriStreams.withOutputStream(destination) { outputStream ->
                exportDataUseCase(cacheDir, outputStream).collect { resId ->
                    updateProgressMessage(UiText.StringResource(resId))
                }
            }
            completeProgress()
        } finally {
            state.isExporting = false
        }
    }

    /**
     * Reads [input] and imports it. The stream is opened here rather than by the screen: opening a
     * content-provider URI is IO, and a composable cannot name a dispatcher.
     */
    suspend fun handleImport(
        input: ImportInput,
        format: ImportFormat,
        options: ImportOptions
    ): Boolean {
        showProgressDialog(UiText.StringResource(R.string.main_activity_loading_file))

        val cacheDir = uriStreams.cacheDirPath()
        val uri = when (input) {
            is ImportInput.FromUri -> input.uri

            // Already a cached file, but routed through the same seam so the open and the close
            // stay on the IO dispatcher.
            is ImportInput.FromPendingImport -> Uri.fromFile(input.pendingImport.file)
        }
        val progressFlow = uriStreams.withInputStream(uri) { inputStream ->
            importDataUseCase(cacheDir, inputStream, format, options)
        }

        return if (progressFlow != null) {
            progressFlow.collect { uiText ->
                updateProgressMessage(uiText)
            }
            hideProgressDialog()
            true
        } else {
            hideProgressDialog()
            false
        }
    }
}

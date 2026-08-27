/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:52 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:49 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.model.ImportOptions
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.sync.PendingImport
import dev.thomas_kiljanczyk.lyriccast.core.sync.PendingImportHolder
import dev.thomas_kiljanczyk.lyriccast.core.sync.use_case.ExportDataUseCase
import dev.thomas_kiljanczyk.lyriccast.core.sync.use_case.ImportDataUseCase
import dev.thomas_kiljanczyk.lyriccast.datatransfer.enums.ImportFormat
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.R
import java.io.InputStream
import java.io.OutputStream
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
    payloadTransport: PayloadTransport
) : ViewModel() {
    private val _state = MutableMainScreenState()
    val state: MainScreenState get() = _state

    /** A file handed in from outside (e.g. via `ACTION_VIEW`/`ACTION_SEND`), if one is waiting. */
    val pendingImport: StateFlow<PendingImport?> = pendingImportHolder.pendingImport

    /** Stops offering the pending file once it has been imported, or the dialog was dismissed. */
    fun clearPendingImport() {
        pendingImportHolder.clear()
    }

    init {
        // Monitor session server status
        payloadTransport.serverIsRunning
            .onEach { _state.isSessionServerRunning = it }
            .launchIn(viewModelScope)
    }

    fun selectTab(tab: MainTab) {
        _state.selectedTab = tab
    }

    fun showProgressDialog(message: UiText) {
        _state.progressMessage = message
        _state.showProgressDialog = true
        _state.progressCompleted = false
    }

    fun updateProgressMessage(message: UiText) {
        _state.progressMessage = message
    }

    fun completeProgress() {
        _state.progressCompleted = true
    }

    fun hideProgressDialog() {
        _state.showProgressDialog = false
        _state.progressMessage = null
        _state.progressCompleted = false
    }

    suspend fun exportAll(cacheDir: String, outputStream: OutputStream) {
        _state.isExporting = true
        try {
            exportDataUseCase(cacheDir, outputStream).collect { resId ->
                updateProgressMessage(UiText.StringResource(resId))
            }
            completeProgress()
        } finally {
            _state.isExporting = false
        }
    }

    suspend fun handleImport(
        cacheDir: String,
        inputStream: InputStream,
        format: ImportFormat,
        options: ImportOptions
    ): Boolean {
        showProgressDialog(UiText.StringResource(R.string.main_activity_loading_file))

        val progressFlow = importDataUseCase(cacheDir, inputStream, format, options)

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

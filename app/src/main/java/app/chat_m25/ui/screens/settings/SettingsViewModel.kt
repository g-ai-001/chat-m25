package app.chat_m25.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.chat_m25.data.repository.BackupRepository
import app.chat_m25.data.repository.ThemeMode
import app.chat_m25.data.repository.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BackupUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val exportResult: String? = null,
    val importResult: String? = null,
    val backupFiles: List<String> = emptyList()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val backupRepository: BackupRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    private val _backupUiState = MutableStateFlow(BackupUiState())
    val backupUiState: StateFlow<BackupUiState> = _backupUiState.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

    fun exportData() {
        viewModelScope.launch {
            _backupUiState.value = _backupUiState.value.copy(isExporting = true, exportResult = null)
            val result = backupRepository.exportData()
            _backupUiState.value = _backupUiState.value.copy(
                isExporting = false,
                exportResult = result.getOrNull() ?: result.exceptionOrNull()?.message
            )
        }
    }

    fun importData(filePath: String) {
        viewModelScope.launch {
            _backupUiState.value = _backupUiState.value.copy(isImporting = true, importResult = null)
            val result = backupRepository.importData(filePath)
            _backupUiState.value = _backupUiState.value.copy(
                isImporting = false,
                importResult = result.getOrNull()?.toString() ?: result.exceptionOrNull()?.message
            )
        }
    }

    fun loadBackupFiles() {
        viewModelScope.launch {
            val files = backupRepository.getBackupFiles().map { it.absolutePath }
            _backupUiState.value = _backupUiState.value.copy(backupFiles = files)
        }
    }

    fun clearExportResult() {
        _backupUiState.value = _backupUiState.value.copy(exportResult = null)
    }

    fun clearImportResult() {
        _backupUiState.value = _backupUiState.value.copy(importResult = null)
    }
}
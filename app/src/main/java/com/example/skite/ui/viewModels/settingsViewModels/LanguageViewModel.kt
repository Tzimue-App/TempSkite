package com.example.skite.ui.viewModels.settingsViewModels

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.example.skite.data.entities.enums.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor() : ViewModel() {

    sealed class UiState {
        object Success : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Success)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentLang = MutableStateFlow(getCurrentLanguage())
    val currentLang = _currentLang.asStateFlow()

    fun updateLanguage(language: AppLanguage) {
        val appLocale = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(appLocale)
        _currentLang.value = language
    }

    fun getCurrentLanguage(): AppLanguage {
        val locale = AppCompatDelegate.getApplicationLocales().get(0)
        return AppLanguage.fromCode(locale?.language ?: "en")
    }
}
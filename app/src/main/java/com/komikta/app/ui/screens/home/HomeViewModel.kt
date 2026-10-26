package com.komikta.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komikta.app.domain.model.HomeContent
import com.komikta.app.domain.model.UiState
import com.komikta.app.domain.repository.ManhwaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ManhwaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeContent>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getHome()
                .onSuccess { content ->
                    if (content.popular.isEmpty() && content.latest.isEmpty() && content.new.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(content)
                    }
                }
                .onFailure { e ->
                    _uiState.value = UiState.Error(e.message ?: "Gagal memuat data")
                }
        }
    }
}

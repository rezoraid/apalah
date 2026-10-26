package com.komikta.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.UiState
import com.komikta.app.domain.repository.ManhwaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: ManhwaRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<UiState<List<Comic>>>(UiState.Empty)
    val results: StateFlow<UiState<List<Comic>>> = _results.asStateFlow()

    private val _recent = MutableStateFlow<List<String>>(emptyList())
    val recent: StateFlow<List<String>> = _recent.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            repository.getRecentSearches().collect { _recent.value = it }
        }
    }

    fun onQueryChange(value: String) {
        _query.value = value
        searchJob?.cancel()
        if (value.isBlank()) {
            _results.value = UiState.Empty
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            performSearch(value)
        }
    }

    fun searchNow() {
        val q = _query.value.trim()
        if (q.isBlank()) return
        searchJob?.cancel()
        viewModelScope.launch { performSearch(q) }
    }

    private suspend fun performSearch(q: String) {
        _results.value = UiState.Loading
        repository.addRecentSearch(q)
        repository.search(q)
            .onSuccess { list ->
                _results.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            }
            .onFailure { e ->
                _results.value = UiState.Error(e.message ?: "Gagal mencari")
            }
    }

    fun useRecent(q: String) {
        _query.value = q
        viewModelScope.launch { performSearch(q) }
    }
}

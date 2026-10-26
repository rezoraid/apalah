package com.komikta.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komikta.app.domain.model.Chapter
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.UiState
import com.komikta.app.domain.repository.ManhwaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailData(
    val comic: Comic,
    val chapters: List<Chapter>
)

class DetailViewModel(
    private val source: String,
    private val sourceId: String,
    private val repository: ManhwaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<DetailData>>(UiState.Loading)
    val uiState: StateFlow<UiState<DetailData>> = _uiState.asStateFlow()

    val isBookmarked: StateFlow<Boolean> = repository
        .isBookmarked("$source:$sourceId")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getComicDetail(source, sourceId)
                .onSuccess { (comic, chapters) ->
                    _uiState.value = UiState.Success(DetailData(comic, chapters))
                }
                .onFailure { e ->
                    _uiState.value = UiState.Error(e.message ?: "Gagal memuat detail")
                }
        }
    }

    fun toggleBookmark() {
        val data = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            if (isBookmarked.value) {
                repository.removeBookmark(data.comic.id)
            } else {
                repository.toggleBookmark(data.comic)
            }
        }
    }
}

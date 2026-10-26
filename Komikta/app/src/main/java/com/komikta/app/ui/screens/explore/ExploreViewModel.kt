package com.komikta.app.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.Genre
import com.komikta.app.domain.model.UiState
import com.komikta.app.domain.repository.ManhwaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val repository: ManhwaRepository
) : ViewModel() {

    private val _comics = MutableStateFlow<UiState<List<Comic>>>(UiState.Loading)
    val comics: StateFlow<UiState<List<Comic>>> = _comics.asStateFlow()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    private var page = 1

    init {
        loadGenres()
        loadComics()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            repository.getGenres().onSuccess { _genres.value = it }
        }
    }

    fun loadComics(genre: String? = _selectedGenre.value) {
        viewModelScope.launch {
            _comics.value = UiState.Loading
            page = 1
            val result = if (genre.isNullOrBlank()) {
                repository.getLatest(page)
            } else {
                repository.getByGenre(genre, page)
            }
            result
                .onSuccess { list ->
                    _comics.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
                }
                .onFailure { e ->
                    _comics.value = UiState.Error(e.message ?: "Gagal memuat")
                }
        }
    }

    fun selectGenre(slug: String?) {
        _selectedGenre.value = slug
        loadComics(slug)
    }
}

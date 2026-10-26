package com.komikta.app.ui.screens.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komikta.app.domain.model.ChapterContent
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.UiState
import com.komikta.app.domain.repository.ManhwaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReaderViewModel(
    private val source: String,
    private val sourceId: String,
    private val chapter: String,
    private val repository: ManhwaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ChapterContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<ChapterContent>> = _uiState.asStateFlow()

    private var comicCache: Comic? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // load detail for comic meta if needed
            repository.getComicDetail(source, sourceId).onSuccess { (comic, _) ->
                comicCache = comic
            }
            repository.getChapter(source, sourceId, chapter)
                .onSuccess { content ->
                    _uiState.value = UiState.Success(content)
                    comicCache?.let { c ->
                        repository.saveReadingProgress(
                            comic = c,
                            chapterId = content.chapterId,
                            chapterNumber = chapter,
                            chapterTitle = content.title,
                            pageIndex = 0
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.value = UiState.Error(e.message ?: "Gagal memuat chapter")
                }
        }
    }
}

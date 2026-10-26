package com.komikta.app.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komikta.app.data.local.entity.ReadingHistoryEntity
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.repository.ManhwaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: ManhwaRepository
) : ViewModel() {

    val bookmarks: StateFlow<List<Comic>> = repository.getBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<ReadingHistoryEntity>> = repository.getReadingHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}

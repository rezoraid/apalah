package com.komikta.app.domain.model

data class Comic(
    val id: String,
    val source: String,
    val sourceId: String,
    val title: String,
    val alternativeTitles: List<String> = emptyList(),
    val type: String? = null,
    val status: String? = null,
    val genres: List<String> = emptyList(),
    val author: String? = null,
    val artist: String? = null,
    val coverUrl: String? = null,
    val synopsis: String? = null,
    val latestChapter: String? = null,
    val rating: Double? = null,
    val updatedAt: String? = null
)

data class Chapter(
    val id: String,
    val comicId: String,
    val source: String,
    val sourceId: String,
    val number: String,
    val title: String?,
    val releaseDate: String?
)

data class ChapterContent(
    val chapterId: String,
    val comicId: String,
    val title: String?,
    val images: List<String>
)

data class Genre(
    val name: String,
    val slug: String,
    val category: String? = null,
    val countHint: String? = null
)

data class HomeContent(
    val popular: List<Comic>,
    val latest: List<Comic>,
    val new: List<Comic>
)

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}

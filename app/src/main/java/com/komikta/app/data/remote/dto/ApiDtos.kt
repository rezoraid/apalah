package com.komikta.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null
)

@Serializable
data class ApiError(
    val code: String? = null,
    val message: String? = null
)

@Serializable
data class HomeData(
    val popular: List<ComicDto> = emptyList(),
    val latest: List<ComicDto> = emptyList(),
    val new: List<ComicDto> = emptyList(),
    val scrapedAt: String? = null
)

@Serializable
data class ListData(
    val items: List<ComicDto> = emptyList(),
    val page: Int? = null,
    val hasMore: Boolean? = null
)

@Serializable
data class ComicDto(
    val id: String,
    val source: String,
    val sourceId: String,
    val slug: String? = null,
    val title: String,
    val alternativeTitles: List<String> = emptyList(),
    val type: String? = null,
    val status: String? = null,
    val genres: List<String> = emptyList(),
    val author: String? = null,
    val artist: String? = null,
    val cover: String? = null,
    val rawCover: String? = null,
    val synopsis: String? = null,
    val latestChapter: String? = null,
    val rating: RatingDto? = null,
    val updatedAt: String? = null
)

@Serializable
data class RatingDto(
    val value: Double? = null,
    val count: Int? = null
)

@Serializable
data class GenreDto(
    val name: String,
    val slug: String,
    val category: String? = null,
    val countHint: String? = null
)

@Serializable
data class ComicDetailData(
    val comic: ComicDto,
    val chapters: List<ChapterDto> = emptyList()
)

@Serializable
data class ChapterDto(
    val id: String,
    val comicId: String? = null,
    val source: String? = null,
    val sourceId: String? = null,
    val chapter: String,
    val title: String? = null,
    val url: String? = null,
    val releaseDate: String? = null,
    val number: String? = null
)

@Serializable
data class ChapterContentData(
    val comic: ComicDto? = null,
    val chapter: ChapterContentDto
)

@Serializable
data class ChapterContentDto(
    val id: String,
    val comicId: String? = null,
    val source: String? = null,
    val sourceId: String? = null,
    val number: String? = null,
    val title: String? = null,
    val type: String? = null,
    val images: List<PageImageDto> = emptyList()
)

@Serializable
data class PageImageDto(
    val index: Int,
    val image: String,
    val rawUrl: String? = null
)

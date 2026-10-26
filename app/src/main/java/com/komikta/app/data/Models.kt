package com.komikta.app.data

data class ApiEnvelope<T>(val success: Boolean = false, val data: T? = null, val error: ApiError? = null)
data class ApiError(val code: String? = null, val message: String? = null)

data class Rating(val value: Double? = null, val count: Int? = null)
data class Comic(
    val id: String = "",
    val source: String = "",
    val sourceId: String = "",
    val slug: String = "",
    val title: String = "Untitled",
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
    val rating: Rating? = null,
    val updatedAt: String? = null
)

data class ComicListData(
    val items: List<Comic> = emptyList(),
    val page: Int = 1,
    val hasNextPage: Boolean = false,
    val popular: List<Comic> = emptyList(),
    val latest: List<Comic> = emptyList(),
    val new: List<Comic> = emptyList()
)

data class Chapter(
    val id: String = "",
    val comicId: String = "",
    val source: String = "",
    val sourceId: String = "",
    val chapter: String = "",
    val title: String = "",
    val releaseDate: String? = null
)
data class DetailData(val comic: Comic = Comic(), val chapters: List<Chapter> = emptyList())
data class ChapterImage(val index: Int = 0, val image: String = "", val rawUrl: String? = null)
data class NovelContent(val paragraphs: List<String> = emptyList())
data class ChapterContent(
    val id: String = "",
    val comicId: String = "",
    val source: String = "",
    val sourceId: String = "",
    val number: String = "",
    val title: String = "",
    val type: String? = null,
    val images: List<ChapterImage> = emptyList(),
    val novelContent: NovelContent? = null,
    val prevChapter: String? = null,
    val nextChapter: String? = null,
    val totalChapters: Int = 0
)
data class ReaderData(
    val comic: Comic = Comic(),
    val chapter: ChapterContent = ChapterContent(),
    val chapters: List<Chapter> = emptyList()
)
data class GenresData(val genres: List<String> = emptyList())

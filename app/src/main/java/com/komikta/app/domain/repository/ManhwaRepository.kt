package com.komikta.app.domain.repository

import com.komikta.app.data.local.entity.ReadingHistoryEntity
import com.komikta.app.domain.model.Chapter
import com.komikta.app.domain.model.ChapterContent
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.Genre
import com.komikta.app.domain.model.HomeContent
import kotlinx.coroutines.flow.Flow

interface ManhwaRepository {
    suspend fun getHome(): Result<HomeContent>
    suspend fun getLatest(page: Int = 1): Result<List<Comic>>
    suspend fun search(query: String, page: Int = 1): Result<List<Comic>>
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun getByGenre(genre: String, page: Int = 1): Result<List<Comic>>
    suspend fun getComicDetail(source: String, sourceId: String): Result<Pair<Comic, List<Chapter>>>
    suspend fun getChapter(source: String, sourceId: String, chapter: String): Result<ChapterContent>

    fun getBookmarks(): Flow<List<Comic>>
    fun isBookmarked(comicId: String): Flow<Boolean>
    suspend fun toggleBookmark(comic: Comic)
    suspend fun removeBookmark(comicId: String)

    fun getReadingHistory(): Flow<List<ReadingHistoryEntity>>
    suspend fun saveReadingProgress(
        comic: Comic,
        chapterId: String,
        chapterNumber: String,
        chapterTitle: String?,
        pageIndex: Int
    )
    suspend fun clearHistory()

    fun getRecentSearches(): Flow<List<String>>
    suspend fun addRecentSearch(query: String)
    suspend fun clearRecentSearches()
}

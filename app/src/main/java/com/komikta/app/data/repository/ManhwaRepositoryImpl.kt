package com.komikta.app.data.repository

import com.komikta.app.data.local.dao.BookmarkDao
import com.komikta.app.data.local.dao.ReadingHistoryDao
import com.komikta.app.data.local.dao.RecentSearchDao
import com.komikta.app.data.local.entity.BookmarkEntity
import com.komikta.app.data.local.entity.ReadingHistoryEntity
import com.komikta.app.data.local.entity.RecentSearchEntity
import com.komikta.app.data.mapper.toDomain
import com.komikta.app.data.remote.api.ManhwaApi
import com.komikta.app.domain.model.Chapter
import com.komikta.app.domain.model.ChapterContent
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.Genre
import com.komikta.app.domain.model.HomeContent
import com.komikta.app.domain.repository.ManhwaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ManhwaRepositoryImpl(
    private val api: ManhwaApi,
    private val bookmarkDao: BookmarkDao,
    private val historyDao: ReadingHistoryDao,
    private val searchDao: RecentSearchDao
) : ManhwaRepository {

    override suspend fun getHome(): Result<HomeContent> = runCatching {
        val resp = api.getHome()
        if (!resp.success || resp.data == null) {
            throw Exception(resp.error?.message ?: "Gagal memuat beranda")
        }
        val data = resp.data
        HomeContent(
            popular = data.popular.map { it.toDomain() },
            latest = data.latest.map { it.toDomain() },
            new = data.new.map { it.toDomain() }
        )
    }

    override suspend fun getLatest(page: Int): Result<List<Comic>> = runCatching {
        val resp = api.getList(page = page)
        if (!resp.success || resp.data == null) {
            throw Exception(resp.error?.message ?: "Gagal memuat daftar")
        }
        resp.data.items.map { it.toDomain() }
    }

    override suspend fun search(query: String, page: Int): Result<List<Comic>> = runCatching {
        val resp = api.search(query = query, page = page)
        if (!resp.success || resp.data == null) {
            throw Exception(resp.error?.message ?: "Gagal mencari")
        }
        resp.data.items.map { it.toDomain() }
    }

    override suspend fun getGenres(): Result<List<Genre>> = runCatching {
        val resp = api.getGenres()
        if (!resp.success || resp.data == null) {
            throw Exception(resp.error?.message ?: "Gagal memuat genre")
        }
        resp.data.map { it.toDomain() }
    }

    override suspend fun getByGenre(genre: String, page: Int): Result<List<Comic>> = runCatching {
        val resp = api.getByGenre(genre = genre, page = page)
        if (!resp.success || resp.data == null) {
            throw Exception(resp.error?.message ?: "Gagal memuat genre")
        }
        resp.data.items.map { it.toDomain() }
    }

    override suspend fun getComicDetail(source: String, sourceId: String): Result<Pair<Comic, List<Chapter>>> =
        runCatching {
            val resp = api.getComicDetail(source, sourceId)
            if (!resp.success || resp.data == null) {
                throw Exception(resp.error?.message ?: "Komik tidak ditemukan")
            }
            val comic = resp.data.comic.toDomain()
            val chapters = resp.data.chapters.map { it.toDomain() }
            comic to chapters
        }

    override suspend fun getChapter(
        source: String,
        sourceId: String,
        chapter: String
    ): Result<ChapterContent> = runCatching {
        val resp = api.getChapter(source, sourceId, chapter)
        if (!resp.success || resp.data == null) {
            throw Exception(resp.error?.message ?: "Chapter tidak ditemukan")
        }
        resp.data.chapter.toDomain(resp.data.comic?.id ?: "$source:$sourceId")
    }

    override fun getBookmarks(): Flow<List<Comic>> =
        bookmarkDao.getAll().map { list ->
            list.map {
                Comic(
                    id = it.comicId,
                    source = it.source,
                    sourceId = it.sourceId,
                    title = it.title,
                    coverUrl = it.coverUrl,
                    type = it.type,
                    status = it.status,
                    latestChapter = it.latestChapter
                )
            }
        }

    override fun isBookmarked(comicId: String): Flow<Boolean> =
        bookmarkDao.isBookmarked(comicId)

    override suspend fun toggleBookmark(comic: Comic) {
        if (bookmarkDao.isBookmarked(comic.id).first()) {
            bookmarkDao.delete(comic.id)
            return
        }
        bookmarkDao.insert(
            BookmarkEntity(
                comicId = comic.id,
                source = comic.source,
                sourceId = comic.sourceId,
                title = comic.title,
                coverUrl = comic.coverUrl,
                type = comic.type,
                status = comic.status,
                latestChapter = comic.latestChapter
            )
        )
    }

    override suspend fun removeBookmark(comicId: String) {
        bookmarkDao.delete(comicId)
    }

    override fun getReadingHistory(): Flow<List<ReadingHistoryEntity>> =
        historyDao.getAll()

    override suspend fun saveReadingProgress(
        comic: Comic,
        chapterId: String,
        chapterNumber: String,
        chapterTitle: String?,
        pageIndex: Int
    ) {
        historyDao.upsert(
            ReadingHistoryEntity(
                comicId = comic.id,
                source = comic.source,
                sourceId = comic.sourceId,
                title = comic.title,
                coverUrl = comic.coverUrl,
                chapterId = chapterId,
                chapterNumber = chapterNumber,
                chapterTitle = chapterTitle,
                pageIndex = pageIndex
            )
        )
    }

    override suspend fun clearHistory() {
        historyDao.clearAll()
    }

    override fun getRecentSearches(): Flow<List<String>> =
        searchDao.getRecent().map { it.map { e -> e.query } }

    override suspend fun addRecentSearch(query: String) {
        if (query.isBlank()) return
        searchDao.insert(RecentSearchEntity(query = query.trim()))
    }

    override suspend fun clearRecentSearches() {
        searchDao.clearAll()
    }
}

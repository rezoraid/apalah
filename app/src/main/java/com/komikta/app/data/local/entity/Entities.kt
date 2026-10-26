package com.komikta.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val comicId: String,
    val source: String,
    val sourceId: String,
    val title: String,
    val coverUrl: String?,
    val type: String?,
    val status: String?,
    val latestChapter: String?,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey val comicId: String,
    val source: String,
    val sourceId: String,
    val title: String,
    val coverUrl: String?,
    val chapterId: String,
    val chapterNumber: String,
    val chapterTitle: String?,
    val pageIndex: Int = 0,
    val lastReadAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long = System.currentTimeMillis()
)

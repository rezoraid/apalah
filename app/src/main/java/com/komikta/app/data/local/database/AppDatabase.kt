package com.komikta.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.komikta.app.data.local.dao.BookmarkDao
import com.komikta.app.data.local.dao.ReadingHistoryDao
import com.komikta.app.data.local.dao.RecentSearchDao
import com.komikta.app.data.local.entity.BookmarkEntity
import com.komikta.app.data.local.entity.ReadingHistoryEntity
import com.komikta.app.data.local.entity.RecentSearchEntity

@Database(
    entities = [
        BookmarkEntity::class,
        ReadingHistoryEntity::class,
        RecentSearchEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun readingHistoryDao(): ReadingHistoryDao
    abstract fun recentSearchDao(): RecentSearchDao
}

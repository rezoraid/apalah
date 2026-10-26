package com.komikta.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.komikta.app.data.local.entity.BookmarkEntity
import com.komikta.app.data.local.entity.ReadingHistoryEntity
import com.komikta.app.data.local.entity.RecentSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY bookmarkedAt DESC")
    fun getAll(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE comicId = :comicId)")
    fun isBookmarked(comicId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE comicId = :comicId")
    suspend fun delete(comicId: String)
}

@Dao
interface ReadingHistoryDao {
    @Query("SELECT * FROM reading_history ORDER BY lastReadAt DESC")
    fun getAll(): Flow<List<ReadingHistoryEntity>>

    @Query("SELECT * FROM reading_history WHERE comicId = :comicId LIMIT 1")
    suspend fun getByComic(comicId: String): ReadingHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ReadingHistoryEntity)

    @Query("DELETE FROM reading_history")
    suspend fun clearAll()

    @Query("DELETE FROM reading_history WHERE comicId = :comicId")
    suspend fun delete(comicId: String)
}

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_searches ORDER BY searchedAt DESC LIMIT 10")
    fun getRecent(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecentSearchEntity)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()
}

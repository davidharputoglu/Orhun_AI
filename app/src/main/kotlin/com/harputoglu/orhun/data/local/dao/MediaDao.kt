package com.harputoglu.orhun.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.harputoglu.orhun.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_history ORDER BY isPinned DESC, timestamp DESC")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Insert
    suspend fun insertMedia(media: MediaEntity)

    @androidx.room.Update
    suspend fun updateMedia(media: MediaEntity)

    @Query("DELETE FROM media_history")
    suspend fun clearHistory()
}

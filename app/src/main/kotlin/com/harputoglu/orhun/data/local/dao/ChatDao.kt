package com.harputoglu.orhun.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.harputoglu.orhun.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_history ORDER BY timestamp DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Insert
    suspend fun insertChat(chat: ChatEntity)

    @Query("DELETE FROM chat_history")
    suspend fun clearHistory()
}

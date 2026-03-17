package com.harputoglu.orhun.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.harputoglu.orhun.data.local.dao.ChatDao
import com.harputoglu.orhun.data.local.entity.ChatEntity

@Database(entities = [ChatEntity::class, NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun noteDao(): NoteDao
}

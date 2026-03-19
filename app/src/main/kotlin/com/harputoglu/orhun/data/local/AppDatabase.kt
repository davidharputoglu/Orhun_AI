package com.harputoglu.orhun.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.harputoglu.orhun.data.local.entity.ChatEntity
import com.harputoglu.orhun.data.local.entity.NoteEntity
import com.harputoglu.orhun.data.local.entity.MediaEntity
import com.harputoglu.orhun.data.local.dao.ChatDao
import com.harputoglu.orhun.data.local.dao.MediaDao
import com.harputoglu.orhun.data.local.dao.NoteDao

@Database(entities = [ChatEntity::class, NoteEntity::class, MediaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun noteDao(): NoteDao
    abstract fun mediaDao(): MediaDao
}

package com.harputoglu.orhun.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_history")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String, // "VIDEO", "IMAGE", "VOICE", "LINK"
    val url: String,
    val timestamp: Long = System.currentTimeMillis(),
    val deviceTarget: String = "TV",
    val isPinned: Boolean = false
)

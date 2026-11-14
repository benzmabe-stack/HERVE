package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String,
    val iconUrl: String = ""
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val fileUrl: String,
    val category: String, // Images, Videos, Music, PDF, Documents, Archives, Other
    val fileSize: String,
    val progress: Int, // 0 to 100
    val status: String, // Completed, Downloading, Paused, Failed
    val localPath: String,
    val timestamp: Long = System.currentTimeMillis()
)

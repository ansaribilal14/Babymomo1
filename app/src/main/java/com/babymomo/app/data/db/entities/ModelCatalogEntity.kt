package com.babymomo.app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_catalog")
data class ModelCatalogEntity(
    @PrimaryKey val id: String,
    val name: String,
    val filename: String,
    @ColumnInfo(name = "sizeBytes") val sizeBytes: Long,
    @ColumnInfo(name = "downloadUrl") val downloadUrl: String,
    @ColumnInfo(name = "isDownloaded") val isDownloaded: Boolean = false,
    @ColumnInfo(name = "isActive") val isActive: Boolean = false,
    @ColumnInfo(name = "downloadedAt") val downloadedAt: Long? = null
)

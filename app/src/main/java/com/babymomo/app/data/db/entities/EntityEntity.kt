package com.babymomo.app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "entities")
data class EntityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val description: String? = null,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "projectId") val projectId: String? = null
)

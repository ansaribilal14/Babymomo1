package com.babymomo.app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mcp_servers")
data class McpServerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    @ColumnInfo(name = "isEnabled") val isEnabled: Boolean = true,
    @ColumnInfo(name = "isCurated") val isCurated: Boolean = false,
    @ColumnInfo(name = "addedAt") val addedAt: Long
)

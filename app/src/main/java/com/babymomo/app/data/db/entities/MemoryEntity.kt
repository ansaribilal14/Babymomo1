package com.babymomo.app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val content: String,
    val type: String,
    val confidence: Double = 1.0,
    @ColumnInfo(name = "hitCount") val hitCount: Int = 0,
    @ColumnInfo(name = "isInSystemPrompt") val isInSystemPrompt: Boolean = false,
    @ColumnInfo(name = "validFrom") val validFrom: Long,
    @ColumnInfo(name = "validTo") val validTo: Long? = null,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "sourceMessageId") val sourceMessageId: String? = null
)

enum class MemoryType(val value: String) {
    WORKING("WORKING"), EPISODIC("EPISODIC"),
    SEMANTIC("SEMANTIC"), PROCEDURAL("PROCEDURAL");
    companion object {
        fun from(value: String) = entries.firstOrNull { it.value == value } ?: SEMANTIC
    }
}

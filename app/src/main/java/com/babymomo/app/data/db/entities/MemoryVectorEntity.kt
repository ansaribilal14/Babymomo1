package com.babymomo.app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "memory_vectors",
    foreignKeys = [ForeignKey(
        entity = MemoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["memoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MemoryVectorEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "memoryId") val memoryId: String,
    val embedding: ByteArray,
    val dimension: Int = 384
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MemoryVectorEntity
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

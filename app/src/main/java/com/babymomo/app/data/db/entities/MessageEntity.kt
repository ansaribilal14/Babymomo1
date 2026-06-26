package com.babymomo.app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = ConversationEntity::class,
        parentColumns = ["id"],
        childColumns = ["conversationId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "conversationId") val conversationId: String,
    val role: String,
    val content: String,
    val timestamp: Long,
    @ColumnInfo(name = "routingReason") val routingReason: String? = null,
    @ColumnInfo(name = "imageUri") val imageUri: String? = null
)

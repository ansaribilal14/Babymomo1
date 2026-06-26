package com.babymomo.app.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "relations",
    foreignKeys = [
        ForeignKey(entity = EntityEntity::class, parentColumns = ["id"], childColumns = ["fromEntityId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = EntityEntity::class, parentColumns = ["id"], childColumns = ["toEntityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class RelationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "fromEntityId") val fromEntityId: String,
    @ColumnInfo(name = "toEntityId") val toEntityId: String,
    val type: String,
    val weight: Double = 1.0,
    @ColumnInfo(name = "validFrom") val validFrom: Long,
    @ColumnInfo(name = "validTo") val validTo: Long? = null
)

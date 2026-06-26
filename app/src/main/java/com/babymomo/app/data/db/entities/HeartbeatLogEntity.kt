package com.babymomo.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heartbeat_log")
data class HeartbeatLogEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val summary: String,
    val notified: Boolean = false,
    val message: String? = null
)

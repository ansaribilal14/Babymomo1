package com.babymomo.app.data.db.dao

import androidx.room.*
import com.babymomo.app.data.db.entities.HeartbeatLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartbeatLogDao {
    @Query("SELECT * FROM heartbeat_log ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<HeartbeatLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HeartbeatLogEntity)
}

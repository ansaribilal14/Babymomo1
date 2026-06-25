package com.babymomo.app.data.db.dao

import androidx.room.*
import com.babymomo.app.data.db.entities.McpServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface McpServerDao {
    @Query("SELECT * FROM mcp_servers ORDER BY addedAt DESC")
    fun getAllServers(): Flow<List<McpServerEntity>>

    @Query("SELECT * FROM mcp_servers WHERE isEnabled = 1")
    suspend fun getEnabledServers(): List<McpServerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(server: McpServerEntity)

    @Update
    suspend fun update(server: McpServerEntity)

    @Delete
    suspend fun delete(server: McpServerEntity)
}

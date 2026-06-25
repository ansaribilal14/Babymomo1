package com.babymomo.app.data.db.dao

import androidx.room.*
import com.babymomo.app.data.db.entities.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories WHERE validTo IS NULL ORDER BY createdAt DESC")
    fun getAllActiveMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE validTo IS NULL AND type = :type ORDER BY createdAt DESC")
    fun getMemoriesByType(type: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isInSystemPrompt = 1 AND validTo IS NULL")
    suspend fun getPromotedMemories(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE id = :id")
    suspend fun getMemory(id: String): MemoryEntity?

    @Query("SELECT * FROM memories WHERE validTo IS NULL AND content LIKE '%' || :query || '%'")
    suspend fun searchMemories(query: String): List<MemoryEntity>

    @Query("SELECT COUNT(*) FROM memories WHERE validTo IS NULL")
    suspend fun getActiveCount(): Int

    @Query("SELECT COUNT(*) FROM memories")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM memories WHERE isInSystemPrompt = 1")
    suspend fun getPromotedCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: MemoryEntity)

    @Update
    suspend fun update(memory: MemoryEntity)

    @Delete
    suspend fun delete(memory: MemoryEntity)
}

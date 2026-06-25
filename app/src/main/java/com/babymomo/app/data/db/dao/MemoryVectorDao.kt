package com.babymomo.app.data.db.dao

import androidx.room.*
import com.babymomo.app.data.db.entities.MemoryVectorEntity

@Dao
interface MemoryVectorDao {
    @Query("SELECT * FROM memory_vectors WHERE memoryId = :memoryId")
    suspend fun getVector(memoryId: String): MemoryVectorEntity?

    @Query("SELECT * FROM memory_vectors")
    suspend fun getAllVectors(): List<MemoryVectorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vector: MemoryVectorEntity)

    @Delete
    suspend fun delete(vector: MemoryVectorEntity)
}

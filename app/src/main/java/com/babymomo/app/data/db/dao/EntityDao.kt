package com.babymomo.app.data.db.dao

import androidx.room.*
import com.babymomo.app.data.db.entities.EntityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntityDao {
    @Query("SELECT * FROM entities ORDER BY createdAt DESC")
    fun getAllEntities(): Flow<List<EntityEntity>>

    @Query("SELECT * FROM entities WHERE id = :id")
    suspend fun getEntity(id: String): EntityEntity?

    @Query("SELECT COUNT(*) FROM entities")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EntityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<EntityEntity>)

    @Delete
    suspend fun delete(entity: EntityEntity)
}

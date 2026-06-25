package com.babymomo.app.data.db.dao

import androidx.room.*
import com.babymomo.app.data.db.entities.RelationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationDao {
    @Query("SELECT * FROM relations WHERE validTo IS NULL")
    fun getAllRelations(): Flow<List<RelationEntity>>

    @Query("SELECT * FROM relations WHERE fromEntityId = :entityId OR toEntityId = :entityId AND validTo IS NULL")
    suspend fun getRelationsForEntity(entityId: String): List<RelationEntity>

    @Query("SELECT COUNT(*) FROM relations WHERE validTo IS NULL")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(relation: RelationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(relations: List<RelationEntity>)

    @Delete
    suspend fun delete(relation: RelationEntity)
}

package com.babymomo.app.data.db.dao

import androidx.room.*
import com.babymomo.app.data.db.entities.ModelCatalogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelCatalogDao {
    @Query("SELECT * FROM model_catalog")
    fun getAllModels(): Flow<List<ModelCatalogEntity>>

    @Query("SELECT * FROM model_catalog WHERE id = :id")
    suspend fun getModel(id: String): ModelCatalogEntity?

    @Query("SELECT * FROM model_catalog WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveModel(): ModelCatalogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: ModelCatalogEntity)

    @Update
    suspend fun update(model: ModelCatalogEntity)
}

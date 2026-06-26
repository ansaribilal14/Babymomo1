package com.babymomo.app.data.db.dao

import androidx.room.*
import com.babymomo.app.data.db.entities.SettingEntity

@Dao
interface SettingDao {
    @Query("SELECT * FROM settings WHERE key = :key")
    suspend fun get(key: String): SettingEntity?

    @Query("SELECT * FROM settings")
    suspend fun getAll(): List<SettingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: SettingEntity)

    @Delete
    suspend fun delete(setting: SettingEntity)
}

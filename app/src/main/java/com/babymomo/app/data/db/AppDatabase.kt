package com.babymomo.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.babymomo.app.data.db.dao.*
import com.babymomo.app.data.db.entities.*

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        MemoryEntity::class,
        MemoryVectorEntity::class,
        EntityEntity::class,
        RelationEntity::class,
        ProjectEntity::class,
        ModelCatalogEntity::class,
        SettingEntity::class,
        HeartbeatLogEntity::class,
        McpServerEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun memoryVectorDao(): MemoryVectorDao
    abstract fun entityDao(): EntityDao
    abstract fun relationDao(): RelationDao
    abstract fun projectDao(): ProjectDao
    abstract fun modelCatalogDao(): ModelCatalogDao
    abstract fun settingDao(): SettingDao
    abstract fun heartbeatLogDao(): HeartbeatLogDao
    abstract fun mcpServerDao(): McpServerDao
}

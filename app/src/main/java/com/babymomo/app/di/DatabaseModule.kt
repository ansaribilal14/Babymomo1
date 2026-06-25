package com.babymomo.app.di

import android.content.Context
import androidx.room.Room
import com.babymomo.app.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val passphrase = "babymomo-secure-key".toByteArray()
        val factory = SupportFactory(passphrase)
        return Room.databaseBuilder(context, AppDatabase::class.java, "babymomo.db")
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideConversationDao(db: AppDatabase) = db.conversationDao()
    @Provides fun provideMessageDao(db: AppDatabase) = db.messageDao()
    @Provides fun provideMemoryDao(db: AppDatabase) = db.memoryDao()
    @Provides fun provideMemoryVectorDao(db: AppDatabase) = db.memoryVectorDao()
    @Provides fun provideEntityDao(db: AppDatabase) = db.entityDao()
    @Provides fun provideRelationDao(db: AppDatabase) = db.relationDao()
    @Provides fun provideProjectDao(db: AppDatabase) = db.projectDao()
    @Provides fun provideModelCatalogDao(db: AppDatabase) = db.modelCatalogDao()
    @Provides fun provideSettingDao(db: AppDatabase) = db.settingDao()
    @Provides fun provideHeartbeatLogDao(db: AppDatabase) = db.heartbeatLogDao()
    @Provides fun provideMcpServerDao(db: AppDatabase) = db.mcpServerDao()
}
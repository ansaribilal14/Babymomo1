package com.babymomo.app.core.memory

import com.babymomo.app.data.db.dao.MemoryDao
import com.babymomo.app.data.db.entities.MemoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryService @Inject constructor(
    private val memoryDao: MemoryDao,
    private val memoryRecaller: MemoryRecaller,
    private val memoryPromoter: MemoryPromoter
) {

    suspend fun recallMemories(query: String, topK: Int = 8): List<RecalledMemory> {
        val recalled = memoryRecaller.recall(query, topK)
        memoryPromoter.checkAndPromote(recalled)
        return recalled
    }

    suspend fun getPromotedMemories(): List<MemoryEntity> {
        return memoryDao.getPromotedMemories()
    }

    suspend fun getActiveCount(): Int = memoryDao.getActiveCount()
    suspend fun getTotalCount(): Int = memoryDao.getTotalCount()
    suspend fun getPromotedCount(): Int = memoryDao.getPromotedCount()
    suspend fun deleteMemory(memory: MemoryEntity) = memoryDao.delete(memory)
    suspend fun getMemory(id: String): MemoryEntity? = memoryDao.getMemory(id)
    suspend fun searchMemories(query: String): List<MemoryEntity> = memoryDao.searchMemories(query)
}

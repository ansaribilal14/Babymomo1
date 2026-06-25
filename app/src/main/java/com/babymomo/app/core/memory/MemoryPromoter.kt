package com.babymomo.app.core.memory

import com.babymomo.app.data.db.dao.MemoryDao
import com.babymomo.app.data.db.entities.MemoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryPromoter @Inject constructor(
    private val memoryDao: MemoryDao
) {

    suspend fun checkAndPromote(memories: List<RecalledMemory>) {
        for (recalled in memories) {
            val mem = recalled.memory
            if (mem.hitCount >= 5 && !mem.isInSystemPrompt) {
                promote(mem)
            } else if (mem.hitCount < 5) {
                val updated = mem.copy(hitCount = mem.hitCount + 1)
                memoryDao.update(updated)
            }
        }
    }

    private suspend fun promote(memory: MemoryEntity) {
        val promoted = memory.copy(
            isInSystemPrompt = true,
            validTo = System.currentTimeMillis()
        )
        memoryDao.update(promoted)
    }
}

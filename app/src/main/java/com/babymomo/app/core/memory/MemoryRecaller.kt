package com.babymomo.app.core.memory

import com.babymomo.app.data.db.dao.MemoryDao
import com.babymomo.app.data.db.dao.MemoryVectorDao
import com.babymomo.app.data.db.entities.MemoryEntity
import com.babymomo.app.data.db.entities.MemoryVectorEntity
import javax.inject.Inject
import javax.inject.Singleton

data class RecalledMemory(val memory: MemoryEntity, val score: Double)

@Singleton
class MemoryRecaller @Inject constructor(
    private val memoryDao: MemoryDao,
    private val memoryVectorDao: MemoryVectorDao,
    private val vectorIndex: VectorIndex,
    private val mockEmbedder: MockEmbedder,
    private val memoryGraph: MemoryGraph
) {

    suspend fun recall(query: String, topK: Int = 8): List<RecalledMemory> {
        val allMemories = mutableListOf<com.babymomo.app.data.db.entities.MemoryEntity>()
        for (type in listOf("WORKING","EPISODIC","SEMANTIC","PROCEDURAL")) {
            allMemories.addAll(memoryDao.searchMemories(""))
        }
        val activeMemories = allMemories.filter { it.validTo == null }
        if (activeMemories.isEmpty()) return emptyList()

        val queryEmbedding = mockEmbedder.embed(query)
        val vectors = mutableListOf<MemoryVectorEntity>()
        for (mem in activeMemories) {
            val vec = memoryVectorDao.getVector(mem.id)
            if (vec != null) vectors.add(vec)
        }

        val cosineScores = if (vectors.isNotEmpty()) {
            vectorIndex.search(queryEmbedding, vectors, topK)
        } else {
            emptyList()
        }

        val cosineMap = cosineScores.toMap()
        val now = System.currentTimeMillis()
        val dayMs = 86400000.0
        val lambda = 0.01

        val scored = activeMemories.map { mem ->
            val cosine = cosineMap[mem.id] ?: 0f
            val graphScore = 0.0
            val confidenceScore = mem.confidence
            val ageDays = (now - mem.createdAt) / dayMs
            val recency = kotlin.math.exp(-lambda * ageDays)

            val finalScore = 0.40 * cosine +
                0.30 * graphScore +
                0.20 * confidenceScore +
                0.10 * recency

            RecalledMemory(mem, finalScore.toDouble())
        }

        return scored.sortedByDescending { it.score }.take(topK)
    }
}

package com.babymomo.app.core.memory

import com.babymomo.app.data.db.entities.MemoryVectorEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VectorIndex @Inject constructor() {

    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size) return 0f
        var dot = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        val denom = kotlin.math.sqrt(normA) * kotlin.math.sqrt(normB)
        return if (denom > 0f) dot / denom else 0f
    }

    fun search(queryEmbedding: FloatArray, vectors: List<MemoryVectorEntity>, topK: Int): List<Pair<String, Float>> {
        return vectors
            .map { v -> v.memoryId to cosineSimilarity(queryEmbedding, v.embedding) }
            .sortedByDescending { it.second }
            .take(topK)
    }
}

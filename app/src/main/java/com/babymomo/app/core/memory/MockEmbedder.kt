package com.babymomo.app.core.memory

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockEmbedder @Inject constructor() {

    fun embed(text: String): FloatArray {
        val dim = 384
        val embedding = FloatArray(dim)
        val hash = text.hashCode().toLong()
        var h = hash
        for (i in 0 until dim) {
            h = (h * 1103515245L + 12345L) and 0x7FFFFFFFL
            embedding[i] = (h % 2000 - 1000) / 1000f
        }
        val norm = kotlin.math.sqrt(embedding.sumOf { it.toDouble() * it.toDouble() }).toFloat()
        if (norm > 0f) for (i in 0 until dim) embedding[i] /= norm
        return embedding
    }
}

package com.babymomo.app.core.memory

import com.babymomo.app.core.llm.WrappedLlmProvider
import com.babymomo.app.data.db.dao.EntityDao
import com.babymomo.app.data.db.dao.MemoryDao
import com.babymomo.app.data.db.dao.MemoryVectorDao
import com.babymomo.app.data.db.dao.RelationDao
import com.babymomo.app.data.db.entities.MemoryEntity
import com.babymomo.app.data.db.entities.MemoryVectorEntity
import kotlinx.serialization.json.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryExtractor @Inject constructor(
    private val llmProvider: WrappedLlmProvider,
    private val memoryDao: MemoryDao,
    private val memoryVectorDao: MemoryVectorDao,
    private val entityDao: EntityDao,
    private val relationDao: RelationDao,
    private val mockEmbedder: MockEmbedder,
    private val vectorIndex: VectorIndex
) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun extractAndStore(userMessage: String, assistantMessage: String, sourceMessageId: String?) {
        val sb = StringBuilder()
        sb.append("You are a memory extraction agent. From the conversation below, extract:\n")
        sb.append("1. ENTITIES: named people, places, projects, or concepts mentioned\n")
        sb.append("2. RELATIONS: how entities relate to each other\n")
        sb.append("3. FACTS: specific facts about the user worth remembering long-term\n")
        sb.append("4. TYPE: classify each fact as WORKING / EPISODIC / SEMANTIC / PROCEDURAL\n\n")
        sb.append("Respond ONLY with JSON. No prose. Schema:\n")
        sb.append("{\n")
        sb.append("  \"entities\": [{\"name\": \"...\"}].\"relations\": [], \"memories\": []\n")
        sb.append("}\n\n")
        sb.append("User: ").append(userMessage).append("\n")
        sb.append("Assistant: ").append(assistantMessage)
        val prompt = sb.toString()

        try {
            val response = llmProvider.complete(prompt)
            val cleaned = response.trim().removePrefix("```json").removeSuffix("```").trim()
            val parsed = json.parseToJsonElement(cleaned).jsonObject

            val entities = parsed["entities"]?.jsonArray ?: emptyList()
            for (ent in entities) {
                val obj = ent.jsonObject
                entityDao.insert(
                    com.babymomo.app.data.db.entities.EntityEntity(
                        id = UUID.randomUUID().toString(),
                        name = obj["name"]?.jsonPrimitive?.content ?: "",
                        type = obj["type"]?.jsonPrimitive?.content ?: "THING",
                        description = obj["description"]?.jsonPrimitive?.content,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }

            val relations = parsed["relations"]?.jsonArray ?: emptyList()
            for (rel in relations) {
                val obj = rel.jsonObject
                val from = obj["from"]?.jsonPrimitive?.content ?: continue
                val to = obj["to"]?.jsonPrimitive?.content ?: continue
                relationDao.insert(
                    com.babymomo.app.data.db.entities.RelationEntity(
                        id = UUID.randomUUID().toString(),
                        fromEntityId = from, toEntityId = to,
                        type = obj["type"]?.jsonPrimitive?.content ?: "RELATED",
                        validFrom = System.currentTimeMillis()
                    )
                )
            }

            val memories = parsed["memories"]?.jsonArray ?: emptyList()
            for (mem in memories) {
                val obj = mem.jsonObject
                val content = obj["content"]?.jsonPrimitive?.content ?: continue
                val typeStr = obj["type"]?.jsonPrimitive?.content ?: "SEMANTIC"
                val confidence = obj["confidence"]?.jsonPrimitive?.doubleOrNull ?: 0.8
                val memId = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()

                memoryDao.insert(
                    MemoryEntity(
                        id = memId, content = content,
                        type = typeStr, confidence = confidence,
                        validFrom = now, createdAt = now,
                        sourceMessageId = sourceMessageId
                    )
                )

                val embedding = mockEmbedder.embed(content)
                memoryVectorDao.insert(
                    MemoryVectorEntity(
                        id = UUID.randomUUID().toString(),
                        memoryId = memId, embedding = vectorIndex.floatArrayToByteArray(embedding)
                    )
                )
            }
        } catch (e: Exception) {
            // Memory extraction is best-effort; log but never crash
        }
    }
}

package com.babymomo.app.core.memory

import com.babymomo.app.data.db.dao.EntityDao
import com.babymomo.app.data.db.dao.RelationDao
import com.babymomo.app.data.db.entities.EntityEntity
import com.babymomo.app.data.db.entities.RelationEntity
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryGraph @Inject constructor(
    private val entityDao: EntityDao,
    private val relationDao: RelationDao
) {

    suspend fun addEntity(name: String, type: String, description: String? = null, projectId: String? = null): EntityEntity {
        val entity = EntityEntity(
            id = UUID.randomUUID().toString(),
            name = name, type = type,
            description = description,
            createdAt = System.currentTimeMillis(),
            projectId = projectId
        )
        entityDao.insert(entity)
        return entity
    }

    suspend fun addRelation(fromId: String, toId: String, type: String, weight: Double = 1.0): RelationEntity {
        val now = System.currentTimeMillis()
        val relation = RelationEntity(
            id = UUID.randomUUID().toString(),
            fromEntityId = fromId, toEntityId = toId,
            type = type, weight = weight,
            validFrom = now
        )
        relationDao.insert(relation)
        return relation
    }

    suspend fun getGraphProximity(entityIds: List<String>, targetEntityId: String): Double {
        val relations = entityIds.flatMap { eid ->
            relationDao.getRelationsForEntity(eid)
        }
        val reachable = mutableSetOf<String>()
        for (r in relations) {
            if (r.fromEntityId in entityIds) reachable.add(r.toEntityId)
            if (r.toEntityId in entityIds) reachable.add(r.fromEntityId)
        }
        return if (targetEntityId in reachable || targetEntityId in entityIds) 1.0 else 0.0
    }

    suspend fun getEntityCount(): Int = entityDao.getCount()
    suspend fun getRelationCount(): Int = relationDao.getCount()
}

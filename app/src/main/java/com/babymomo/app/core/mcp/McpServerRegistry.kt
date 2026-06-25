package com.babymomo.app.core.mcp

import com.babymomo.app.data.db.dao.McpServerDao
import com.babymomo.app.data.db.entities.McpServerEntity
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class McpServerRegistry @Inject constructor(
    private val mcpServerDao: McpServerDao
) {
    suspend fun addServer(name: String, url: String) {
        mcpServerDao.insert(McpServerEntity(
            id = UUID.randomUUID().toString(), name = name, url = url,
            addedAt = System.currentTimeMillis()
        ))
    }

    suspend fun getEnabledServers(): List<McpServerEntity> = mcpServerDao.getEnabledServers()

    fun getCuratedServers(): List<McpServerEntity> = listOf(
        McpServerEntity(id = "fetch", name = "Fetch", url = "https://mcp.fetch.ai", isCurated = true, addedAt = 0),
        McpServerEntity(id = "deepwiki", name = "DeepWiki", url = "https://mcp.deepwiki.org", isCurated = true, addedAt = 0)
    )
}

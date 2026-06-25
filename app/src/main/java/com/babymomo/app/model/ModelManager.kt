package com.babymomo.app.model

import com.babymomo.app.core.llm.LocalLlmProvider
import com.babymomo.app.data.db.dao.ModelCatalogDao
import com.babymomo.app.data.db.entities.ModelCatalogEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelManager @Inject constructor(
    private val modelCatalogDao: ModelCatalogDao,
    private val localLlmProvider: LocalLlmProvider
) {
    suspend fun initializeCatalog() {
        val catalog = ModelCatalog().toEntities()
        for (model in catalog) {
            val existing = modelCatalogDao.getModel(model.id)
            if (existing == null) modelCatalogDao.insert(model)
        }
    }

    suspend fun activateModel(modelId: String) {
        // Activation logic is handled via ModelCatalogDao updates
    }
}

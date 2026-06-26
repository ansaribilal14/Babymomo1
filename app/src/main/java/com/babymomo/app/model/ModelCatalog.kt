package com.babymomo.app.model

import com.babymomo.app.data.db.entities.ModelCatalogEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelCatalog @Inject constructor() {

    data class ModelEntry(
        val id: String, val name: String, val filename: String,
        val sizeBytes: Long, val downloadUrl: String
    )

    val catalog = listOf(
        ModelEntry("gemma-2b", "Gemma 2B IT", "gemma-2b-it.bin",
            1_500_000_000L, "https://dl.google.com/aitk/litert/models/gemma-2b-it.litert.bin"),
        ModelEntry("phi-3-mini", "Phi-3 Mini 3.8B", "phi-3-mini-3.8b.bin",
            2_400_000_000L, "https://dl.google.com/aitk/litert/models/phi-3-mini.litert.bin")
    )

    fun toEntities(): List<ModelCatalogEntity> = catalog.map { ModelCatalogEntity(
        id = it.id, name = it.name, filename = it.filename,
        sizeBytes = it.sizeBytes, downloadUrl = it.downloadUrl
    )}
}

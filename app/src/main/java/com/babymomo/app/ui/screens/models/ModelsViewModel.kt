package com.babymomo.app.ui.screens.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babymomo.app.data.db.dao.ModelCatalogDao
import com.babymomo.app.data.db.entities.ModelCatalogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelsViewModel @Inject constructor(
    private val modelCatalogDao: ModelCatalogDao
) : ViewModel() {
    val models: StateFlow<List<ModelCatalogEntity>> = modelCatalogDao.getAllModels()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun activateModel(modelId: String) {
        viewModelScope.launch {
            val all = modelCatalogDao.getAllModels().first()
            all.forEach { m ->
                modelCatalogDao.update(m.copy(isActive = m.id == modelId))
            }
        }
    }

    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            val model = modelCatalogDao.getModel(modelId) ?: return@launch
            modelCatalogDao.update(model.copy(isDownloaded = true, downloadedAt = System.currentTimeMillis()))
        }
    }
}

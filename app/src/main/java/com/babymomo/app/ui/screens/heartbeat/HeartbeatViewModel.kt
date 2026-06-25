package com.babymomo.app.ui.screens.heartbeat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babymomo.app.data.db.dao.HeartbeatLogDao
import com.babymomo.app.data.db.entities.HeartbeatLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HeartbeatViewModel @Inject constructor(
    private val heartbeatLogDao: HeartbeatLogDao
) : ViewModel() {
    val logs: StateFlow<List<HeartbeatLogEntity>> = heartbeatLogDao.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun triggerManual() {
        viewModelScope.launch {
            heartbeatLogDao.insert(HeartbeatLogEntity(
                id = UUID.randomUUID().toString(), timestamp = System.currentTimeMillis(),
                summary = "MANUAL TRIGGER - SILENT", notified = false
            ))
        }
    }
}

package com.babymomo.app.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.babymomo.app.BabymomoApp
import com.babymomo.app.core.llm.WrappedLlmProvider
import com.babymomo.app.data.db.dao.HeartbeatLogDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class HeartbeatWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val wrappedLlmProvider: WrappedLlmProvider,
    private val heartbeatLogDao: HeartbeatLogDao
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        if (hour < 8 || hour >= 22) {
            logResult("SILENT (outside active window 8am-10pm)", false, null)
            return Result.success()
        }

        val prompt = "You are Babymomo's autonomous background agent. Review recent context. Respond with SILENT or a short notification."
        try {
            val response = wrappedLlmProvider.complete(prompt)
            val isSilent = response.trim().equals("SILENT", ignoreCase = true)
            logResult(if (isSilent) "SILENT" else "NOTIFIED", !isSilent, if (isSilent) null else response)
        } catch (e: Exception) {
            logResult("ERROR: ${e.message}", false, null)
        }
        return Result.success()
    }

    private suspend fun logResult(summary: String, notified: Boolean, message: String?) {
        heartbeatLogDao.insert(
            com.babymomo.app.data.db.entities.HeartbeatLogEntity(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                summary = summary, notified = notified, message = message
            )
        )
    }
}

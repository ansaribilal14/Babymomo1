package com.babymomo.app.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.withProgress
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ModelDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val modelId = inputData.getString("model_id") ?: return Result.failure()
        for (i in 0..100 step 10) {
            withProgress(i / 100f)
            kotlinx.coroutines.delay(200)
        }
        return Result.success()
    }
}

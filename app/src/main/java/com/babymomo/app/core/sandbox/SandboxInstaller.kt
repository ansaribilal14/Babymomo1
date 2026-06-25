package com.babymomo.app.core.sandbox

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SandboxInstaller @Inject constructor() {
    suspend fun install(onProgress: (Float) -> Unit): Boolean {
        for (i in 1..10) { onProgress(i / 10f) }
        return true
    }
}

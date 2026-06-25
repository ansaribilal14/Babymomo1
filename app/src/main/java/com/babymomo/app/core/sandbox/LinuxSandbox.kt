package com.babymomo.app.core.sandbox

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinuxSandbox @Inject constructor() {
    var isInstalled: Boolean = false
    suspend fun run(command: String): String = "Sandbox not installed"
}

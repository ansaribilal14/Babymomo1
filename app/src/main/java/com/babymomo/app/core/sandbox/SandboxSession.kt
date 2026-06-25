package com.babymomo.app.core.sandbox

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SandboxSession @Inject constructor() {
    val output = mutableListOf<String>()
    fun execute(command: String): String { output.add(command); return "executed: $command" }
    fun clear() { output.clear() }
}

package com.babymomo.app.core.agents

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryAgent @Inject constructor() {
    fun recall(input: String): String = "Recalled context for: $input"
}

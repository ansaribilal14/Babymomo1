package com.babymomo.app.core.agents

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlannerAgent @Inject constructor() {
    fun plan(input: String): String = "Plan for: $input"
}

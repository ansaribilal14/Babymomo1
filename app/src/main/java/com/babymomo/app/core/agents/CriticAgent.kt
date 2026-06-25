package com.babymomo.app.core.agents

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CriticAgent @Inject constructor() {
    fun critique(plan: String, research: String): String = "Critique of plan and research"
}

package com.babymomo.app.core.agents

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResearcherAgent @Inject constructor() {
    fun research(plan: String): String = "Research on: $plan"
}

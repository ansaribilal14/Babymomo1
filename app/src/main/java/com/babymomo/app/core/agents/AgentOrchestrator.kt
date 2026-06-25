package com.babymomo.app.core.agents

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentOrchestrator @Inject constructor(
    private val plannerAgent: PlannerAgent,
    private val researcherAgent: ResearcherAgent,
    private val memoryAgent: MemoryAgent,
    private val criticAgent: CriticAgent,
    private val executorAgent: ExecutorAgent
) {
    fun orchestrate(input: String, context: String = ""): String {
        val plan = plannerAgent.plan(input)
        val research = researcherAgent.research(plan)
        val memories = memoryAgent.recall(input)
        val critique = criticAgent.critique(plan, research)
        val result = executorAgent.execute(critique)
        return result
    }
}

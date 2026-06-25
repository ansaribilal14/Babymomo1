package com.babymomo.app.core.skills

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummarizeSkill @Inject constructor() : Skill {
    override val name = "Summarize"
    override val triggers = listOf("summarize", "tldr", "give me a summary")
    override suspend fun execute(input: String): String = "Summary: $input"
}

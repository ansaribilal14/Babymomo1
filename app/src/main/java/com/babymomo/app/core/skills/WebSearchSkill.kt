package com.babymomo.app.core.skills

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSearchSkill @Inject constructor() : Skill {
    override val name = "WebSearch"
    override val triggers = listOf("search", "look up", "find on web", "google")
    override suspend fun execute(input: String): String = "Web search: $input"
}

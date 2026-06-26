package com.babymomo.app.core.agents

import com.babymomo.app.core.skills.SkillRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExecutorAgent @Inject constructor(
    private val skillRegistry: SkillRegistry
) {
    fun execute(input: String): String {
        val skill = skillRegistry.matchSkill(input)
        return if (skill != null) {
            "Skill triggered: ${skill.javaClass.simpleName}"
        } else {
            input
        }
    }
}

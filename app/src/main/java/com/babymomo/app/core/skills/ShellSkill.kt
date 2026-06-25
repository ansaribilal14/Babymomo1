package com.babymomo.app.core.skills

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShellSkill @Inject constructor() : Skill {
    override val name = "Shell"
    override val triggers = listOf("run command", "shell", "execute", "terminal")
    override suspend fun execute(input: String): String = "Shell exec: $input"
}

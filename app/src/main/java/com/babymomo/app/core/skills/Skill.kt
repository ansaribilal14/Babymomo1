package com.babymomo.app.core.skills

interface Skill {
    val name: String
    val triggers: List<String>
    suspend fun execute(input: String): String
}

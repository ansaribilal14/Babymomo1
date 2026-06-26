package com.babymomo.app.core.skills

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarSkill @Inject constructor() : Skill {
    override val name = "Calendar"
    override val triggers = listOf("calendar", "schedule", "event", "meeting")
    override suspend fun execute(input: String): String = "Calendar: $input"
}

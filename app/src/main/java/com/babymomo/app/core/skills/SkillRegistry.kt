package com.babymomo.app.core.skills
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillRegistry @Inject constructor(
    private val webSearchSkill: WebSearchSkill,
    private val writeArticleSkill: WriteArticleSkill,
    private val summarizeSkill: SummarizeSkill,
    private val calendarSkill: CalendarSkill,
    private val shellSkill: ShellSkill,
    private val pdfSkill: PdfSkill
) {
    private val skills: List<Skill> = listOf(
        webSearchSkill, writeArticleSkill, summarizeSkill,
        calendarSkill, shellSkill, pdfSkill
    )

    fun matchSkill(input: String): Skill? {
        val lower = input.lowercase()
        return skills.firstOrNull { skill ->
            skill.triggers.any { trigger -> lower.contains(trigger) }
        }
    }

    fun getAllSkills(): List<Skill> = skills
}

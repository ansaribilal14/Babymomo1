package com.babymomo.app.core.skills

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WriteArticleSkill @Inject constructor() : Skill {
    override val name = "WriteArticle"
    override val triggers = listOf("write an article", "write a blog", "compose an essay")
    override suspend fun execute(input: String): String = "Writing article about: $input"
}

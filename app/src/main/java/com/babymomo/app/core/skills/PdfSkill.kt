package com.babymomo.app.core.skills

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfSkill @Inject constructor() : Skill {
    override val name = "Pdf"
    override val triggers = listOf("pdf", "document analysis")
    override suspend fun execute(input: String): String =
        "PDF analysis is a stub in v1.0. Full document ingestion will be available in v1.1."
}

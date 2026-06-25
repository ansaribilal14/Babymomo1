package com.babymomo.app.core.kernel

enum class RequestType {
    CHAT, SKILL, AGENT, INTERACTIVE
}

class RequestClassifier {

    fun classify(input: String): RequestType {
        val lower = input.lowercase()
        val interactiveKeywords = listOf("quiz", "dashboard", "recipe card", "game", "brainstorm board", "poll", "survey", "interactive")
        val skillKeywords = listOf("write an article", "summarize", "search the web", "check calendar", "run command", "shell")

        if (interactiveKeywords.any { lower.contains(it) }) return RequestType.INTERACTIVE
        if (skillKeywords.any { lower.contains(it) }) return RequestType.SKILL
        if (lower.length > 200) return RequestType.AGENT
        return RequestType.CHAT
    }
}

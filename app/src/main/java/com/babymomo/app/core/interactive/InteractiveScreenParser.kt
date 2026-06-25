package com.babymomo.app.core.interactive

import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InteractiveScreenParser @Inject constructor() {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(jsonString: String): ScreenDescriptor? {
        return try { json.decodeFromString<ScreenDescriptor>(jsonString) }
        catch (_: Exception) { null }
    }
}

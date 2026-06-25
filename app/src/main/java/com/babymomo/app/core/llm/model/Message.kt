package com.babymomo.app.core.llm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val role: String,
    val content: String,
    @SerialName("image_uri") val imageUri: String? = null
) {
    companion object {
        fun user(text: String) = Message("user", text)
        fun assistant(text: String) = Message("assistant", text)
        fun tool(text: String) = Message("tool", text)
    }
}

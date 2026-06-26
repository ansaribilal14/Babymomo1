package com.babymomo.app.core.interactive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScreenDescriptor(
    val mode: String = "fullscreen",
    val title: String = "",
    val widgets: List<BabyWidget> = emptyList(),
    val actions: Map<String, String> = emptyMap()
)

@Serializable
sealed class BabyWidget {
    @Serializable @SerialName("text")
    data class BabyText(val text: String, val style: String = "body") : BabyWidget()

    @Serializable @SerialName("button")
    data class BabyButton(val label: String, val actionId: String) : BabyWidget()

    @Serializable @SerialName("list")
    data class BabyList(val items: List<BabyListItem>) : BabyWidget()

    @Serializable @SerialName("input")
    data class BabyInput(val hint: String, val inputId: String) : BabyWidget()

    @Serializable @SerialName("card")
    data class BabyCard(val title: String, val body: String, val children: List<BabyWidget> = emptyList()) : BabyWidget()

    @Serializable @SerialName("grid")
    data class BabyGrid(val columns: Int, val children: List<BabyWidget>) : BabyWidget()

    @Serializable @SerialName("progress")
    data class BabyProgress(val value: Int, val max: Int, val label: String) : BabyWidget()

    @Serializable @SerialName("divider")
    object BabyDivider : BabyWidget()
}

@Serializable
data class BabyListItem(val text: String, val actionId: String? = null)

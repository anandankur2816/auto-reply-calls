package com.autoreply.calls.data

/**
 * Enum representing the available status types with their display names and default messages.
 */
enum class StatusType(
    val displayName: String,
    val emoji: String,
    val defaultMessage: String
) {
    LUNCH(
        displayName = "At Lunch",
        emoji = "🍽️",
        defaultMessage = "Hey! I'm at lunch right now. Will call you back shortly."
    ),
    MEETING(
        displayName = "In Meeting",
        emoji = "📋",
        defaultMessage = "I'm currently in a meeting and can't take calls. I'll get back to you as soon as I'm free."
    ),
    DRIVING(
        displayName = "Driving",
        emoji = "🚗",
        defaultMessage = "I'm driving right now and can't answer. I'll call you back when I reach safely."
    ),
    BUSY(
        displayName = "Busy",
        emoji = "🔕",
        defaultMessage = "I'm a bit busy at the moment. Will return your call soon!"
    ),
    CUSTOM(
        displayName = "Custom",
        emoji = "✍️",
        defaultMessage = ""
    );

    fun getFullDisplayName(): String = "$emoji $displayName"
}

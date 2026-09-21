package com.example.message

import java.util.Calendar

data class DailyMessage(
    val id: String,
    val text: String,
    val category: String,
    val contextNote: String
)

object TodayMessageEngine {
    private val messages = listOf(
        // MORNING
        DailyMessage(
            id = "m1",
            text = "Do what needs doing. Then put the phone down.",
            category = "MORNING",
            contextNote = "First light clarity"
        ),
        DailyMessage(
            id = "m2",
            text = "Before you open anything, decide what you came here to do.",
            category = "MORNING",
            contextNote = "Intentional starting point"
        ),
        DailyMessage(
            id = "m3",
            text = "Your attention is your only non-renewable asset.",
            category = "MORNING",
            contextNote = "Dawn perspective"
        ),

        // WORK
        DailyMessage(
            id = "w1",
            text = "Finish one thing before opening another.",
            category = "WORK",
            contextNote = "Single-task focus"
        ),
        DailyMessage(
            id = "w2",
            text = "Nothing urgent is happening. Respond to what matters. Ignore the rest.",
            category = "WORK",
            contextNote = "Triage rule"
        ),
        DailyMessage(
            id = "w3",
            text = "A tool serves the hand. The hand does not serve the tool.",
            category = "WORK",
            contextNote = "Instrument awareness"
        ),

        // FOCUS
        DailyMessage(
            id = "f1",
            text = "The urge to check will pass in twenty seconds if you do not feed it.",
            category = "FOCUS",
            contextNote = "Impulse dissipation"
        ),
        DailyMessage(
            id = "f2",
            text = "Depth requires uninterrupted silence.",
            category = "FOCUS",
            contextNote = "Attention threshold"
        ),

        // SOCIAL
        DailyMessage(
            id = "s1",
            text = "Every feed is engineered to feel incomplete. You are allowed to leave.",
            category = "SOCIAL",
            contextNote = "Feed architecture"
        ),
        DailyMessage(
            id = "s2",
            text = "A message from a real person is signal. An algorithm is noise.",
            category = "SOCIAL",
            contextNote = "Connection filter"
        ),

        // EVENING
        DailyMessage(
            id = "e1",
            text = "The day is already over. The screen has nothing new to give you.",
            category = "EVENING",
            contextNote = "Twilight boundary"
        ),
        DailyMessage(
            id = "e2",
            text = "Your phone can wait until tomorrow.",
            category = "EVENING",
            contextNote = "Rest state"
        ),

        // RESET / WEEKEND
        DailyMessage(
            id = "r1",
            text = "Space between inputs is where original thought happens.",
            category = "RESET",
            contextNote = "Cognitive recovery"
        ),
        DailyMessage(
            id = "r2",
            text = "Look up. The physical world is still operating at full resolution.",
            category = "WEEKEND",
            contextNote = "Perceptual return"
        )
    )

    fun getMessageForCurrentContext(isFocusActive: Boolean = false, overrideIndex: Int? = null): DailyMessage {
        if (isFocusActive) {
            return messages.first { it.category == "FOCUS" }
        }

        if (overrideIndex != null) {
            return messages[overrideIndex % messages.size]
        }

        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

        val targetCategory = when {
            dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY -> "WEEKEND"
            hour in 5..10 -> "MORNING"
            hour in 11..17 -> "WORK"
            hour in 18..22 -> "EVENING"
            else -> "RESET"
        }

        val matching = messages.filter { it.category == targetCategory }
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        return matching[dayOfYear % matching.size]
    }

    fun getAllMessages(): List<DailyMessage> = messages
}

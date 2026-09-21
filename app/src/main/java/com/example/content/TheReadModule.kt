package com.example.content

data class ReadQuestion(
    val step: Int,
    val prompt: String,
    val subtext: String,
    val optionA: String,
    val optionB: String,
    val weightA: Int,
    val weightB: Int
)

data class ReadResultProfile(
    val archetype: String,
    val title: String,
    val summary: String,
    val score: Int
)

object TheReadEngine {
    val questions = listOf(
        ReadQuestion(
            step = 1,
            prompt = "When a notification lights up the screen, what is your involuntary physical response?",
            subtext = "Notice the somatic reaction before the thought.",
            optionA = "A subtle spike of adrenaline or urgency to check immediately.",
            optionB = "Indifference or deliberate postponement until a convenient time.",
            weightA = 1,
            weightB = 3
        ),
        ReadQuestion(
            step = 2,
            prompt = "How do you navigate moments of unexpected idle waiting?",
            subtext = "Elevator doors, crosswalk lights, waiting in line.",
            optionA = "My hand reflexively reaches for the glass rectangle.",
            optionB = "I observe the physical space and people around me.",
            weightA = 1,
            weightB = 3
        ),
        ReadQuestion(
            step = 3,
            prompt = "What is your relationship with digital feeds?",
            subtext = "The infinite vertical scroll mechanism.",
            optionA = "I open them with intent, but lose track of time within minutes.",
            optionB = "I extract the specific item I need and immediately exit.",
            weightA = 1,
            weightB = 3
        ),
        ReadQuestion(
            step = 4,
            prompt = "How do you experience silence in your daily environment?",
            subtext = "Without podcasts, video backgrounds, or ambient streams.",
            optionA = "Uncomfortable; I prefer continuous auditory stimulation.",
            optionB = "Restorative; silence allows original thoughts to coalesce.",
            weightA = 1,
            weightB = 3
        ),
        ReadQuestion(
            step = 5,
            prompt = "When you close this phone right now, what awaits your attention?",
            subtext = "The physical reality beyond the glass.",
            optionA = "A fragmented backlog of pending digital demands.",
            optionB = "A focused, deliberate reality that I actively choose.",
            weightA = 1,
            weightB = 3
        )
    )

    fun calculateResult(answers: List<Int>): ReadResultProfile {
        val totalScore = answers.sum()

        return when {
            totalScore >= 13 -> ReadResultProfile(
                archetype = "THE SOVEREIGN OPERATOR",
                title = "HIGH ATTENTION AUTONOMY",
                summary = "You treat technology as a sharp, deliberate instrument. Your boundaries between the physical realm and the digital stream are well-fortified.",
                score = totalScore
            )
            totalScore >= 9 -> ReadResultProfile(
                archetype = "THE TRANSITIONAL OBSERVER",
                title = "AWARE BUT INTERRUPTIBLE",
                summary = "You possess conscious awareness of algorithmic capture, yet reflexive habits occasionally erode your intentionality. UNPLUG OS serves as your buffer.",
                score = totalScore
            )
            else -> ReadResultProfile(
                archetype = "THE CAPTURED INTELLECT",
                title = "HIGH ATTENTION FRAGMENTATION",
                summary = "Your device has subtly migrated from a tool you control to an environment that commands your nervous system. Minimalist friction will restore your baseline.",
                score = totalScore
            )
        }
    }
}

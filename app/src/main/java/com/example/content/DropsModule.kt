package com.example.content

data class DropDetail(
    val dropNumber: String,
    val title: String,
    val subtitle: String,
    val domain: String,
    val interactiveType: String,
    val content: String,
    val directive: String
)

object DropsEngine {
    val dropList = listOf(
        DropDetail(
            dropNumber = "DROP 001",
            title = "THE WAIT",
            subtitle = "30 Seconds of Intentional Stillness",
            domain = "PERCEPTION",
            interactiveType = "STILLNESS_TIMER",
            content = "The modern phone creates an illusion of zero-latency urgency. When you feel the reflex to open a dopamine feed, wait exactly thirty seconds without touching the screen. Watch the neurological itch peak, plateau, and subside.",
            directive = "Initiate the stillness ring. Do not leave until the circle completes."
        ),
        DropDetail(
            dropNumber = "DROP 002",
            title = "THE CHOICE",
            subtitle = "Cognitive Cost Tally",
            domain = "DECISION",
            interactiveType = "COGNITIVE_TRADE",
            content = "Every hour spent inside an algorithm is an irreversible trade of physical consciousness. You didn't just watch videos; you traded reading a book, walking outdoors, or deep sleep.",
            directive = "Select what you intend to build today instead of consuming."
        ),
        DropDetail(
            dropNumber = "DROP 003",
            title = "THE SIGNAL",
            subtitle = "Frequency Filter",
            domain = "ATTENTION",
            interactiveType = "SIGNAL_SEPARATOR",
            content = "Notifications are divided into two fundamental physics: Human Signals (direct communication from people you know) and Synthetic Traps (engagement reminders engineered by machines).",
            directive = "Mute synthetic notifications and protect human signal lines."
        ),
        DropDetail(
            dropNumber = "DROP 004",
            title = "THE SILENCE",
            subtitle = "Dark Canvas Calibration",
            domain = "RESET",
            interactiveType = "SENSORY_RESET",
            content = "Visual clutter is cognitive friction. The brain processes high-contrast neon icons as active threats and stimuli. OFFLINE OS strips the circus away to give your eyes rest.",
            directive = "Hold the center of the dark canvas to recalibrate sensory baseline."
        ),
        DropDetail(
            dropNumber = "DROP 005",
            title = "THE THRESHOLD",
            subtitle = "The Edge of the Glass",
            domain = "PHYSICAL",
            interactiveType = "SENSORY_GROUNDING",
            content = "Feel the cold metallic rim of your device right now. Notice the weight of the hardware in your palm. It is merely glass and silicon. You are the sentient consciousness holding it.",
            directive = "Ground yourself in the physical room before returning home."
        )
    )

    fun getDrop(dropNumber: String): DropDetail? = dropList.firstOrNull { it.dropNumber == dropNumber }
}

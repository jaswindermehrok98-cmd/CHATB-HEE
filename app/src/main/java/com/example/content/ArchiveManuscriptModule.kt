package com.example.content

data class ManuscriptChapter(
    val number: String,
    val title: String,
    val readTime: String,
    val text: String
)

object ArchiveManuscriptEngine {
    val chapters = listOf(
        ManuscriptChapter(
            number = "01",
            title = "THE ARCHITECTURE OF CAPTURE",
            readTime = "3 MIN READ",
            text = """
                In 2007, the pocket computer was introduced as a bicycle for the mind. Within a decade, business models shifted from selling software tools to harvesting human attention units.
                
                The smartphone ceased being a passive tool like a hammer or a piano. It transformed into an active slot machine, vibrating in pockets to summon its operator back into ad-supported auction streams.
                
                Every notification badge is carefully colored in urgent crimson (#FF3B30) because the primate visual cortex interprets red as danger, ripe fruit, or blood.
                
                To reclaim your intellectual autonomy, you must intentionally strip the dopamine triggers from the surface.
            """.trimIndent()
        ),
        ManuscriptChapter(
            number = "02",
            title = "THE MYTH OF MULTITASKING",
            readTime = "4 MIN READ",
            text = """
                The human prefrontal cortex cannot process multiple high-order cognitive streams concurrently. What is commonly termed multitasking is neurological rapid-switching.
                
                Each switch between an email, a chat bubble, and a work document incurs an 'attention residue' penalty that persists for up to 23 minutes.
                
                When your home screen presents 40 brightly colored icons, your brain expends glucose evaluating possibilities before you even touch the glass.
                
                OFFLINE OS limits the visual field to what matters now: Time, Today's Message, Direct Communication, and Intentional Search.
            """.trimIndent()
        ),
        ManuscriptChapter(
            number = "03",
            title = "THE SOVEREIGN OPERATOR",
            readTime = "3 MIN READ",
            text = """
                A sovereign operator does not flee from modern tools. They master them with cold, precise discipline.
                
                You do not need to throw your phone into the sea. You need only make the entry into apps intentional rather than impulsive.
                
                Set your daily limits. Triage messages without wading into feeds. Use quick replies directly from the launcher. Then put the phone facedown on the table and return to the room you inhabit.
            """.trimIndent()
        )
    )
}

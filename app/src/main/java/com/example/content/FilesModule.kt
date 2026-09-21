package com.example.content

data class UnplugFile(
    val path: String,
    val name: String,
    val size: String,
    val type: String,
    val content: String
)

object FilesEngine {
    val fileTree = listOf(
        UnplugFile(
            path = "/UNPLUG/SYSTEM/MANIFESTO.TXT",
            name = "MANIFESTO.TXT",
            size = "2.4 KB",
            type = "DOCUMENT",
            content = """
                UNPLUG LABS® OFFLINE OS
                ========================
                
                The phone is an instrument.
                An instrument is picked up to perform a deliberate action, and set down when that action is complete.
                
                When software is funded by human attention, software becomes predatory by necessity.
                Every infinite scroll, push notification red dot, and autoplay carousel is engineered to exploit the human dopamine loop.
                
                OFFLINE OS is the antidote.
                - Obsidian surfaces to soothe the ocular nerve.
                - Typography over icon walls.
                - Direct communication triage without app rabbit-holes.
                - Strict limits to protect sovereignty.
                
                Own your attention.
            """.trimIndent()
        ),
        UnplugFile(
            path = "/UNPLUG/READ/CALIBRATION.DAT",
            name = "CALIBRATION.DAT",
            size = "1.1 KB",
            type = "SYSTEM_DATA",
            content = "LOCAL ENCRYPTION: ACTIVE\nSYNC STATUS: ISOLATED (AIRPLANE MODE VERIFIED)\nNO TELEMETRY EXPORTED."
        ),
        UnplugFile(
            path = "/UNPLUG/DROPS/INDEX.LOG",
            name = "INDEX.LOG",
            size = "3.8 KB",
            type = "LOG",
            content = "001 // THE WAIT [ACTIVE]\n002 // THE CHOICE [ACTIVE]\n003 // THE SIGNAL [STANDBY]\n004 // THE SILENCE [STANDBY]\n005 // THE THRESHOLD [LOCKED]"
        ),
        UnplugFile(
            path = "/UNPLUG/ARCHIVE/CHAPTER_01.MD",
            name = "CHAPTER_01.MD",
            size = "4.2 KB",
            type = "MANUSCRIPT",
            content = "# THE ATTENTION MACHINE\n\nThe fundamental conflict of the 21st century is between human agency and algorithmic optimization. We have outsourced memory, direction, and silence to cloud servers. The reclaiming begins with the launcher."
        )
    )
}

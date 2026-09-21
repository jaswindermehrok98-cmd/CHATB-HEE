package com.example.content

data class LabExperiment(
    val id: String,
    val code: String,
    val title: String,
    val domain: String,
    val description: String,
    val expectedOutcome: String
)

object LabEngine {
    val experiments = listOf(
        LabExperiment(
            id = "exp_time",
            code = "EXP 01",
            title = "TEMPORAL PERCEPTION",
            domain = "TIME",
            description = "Estimate a blind interval of 10.00 seconds without looking at a clock. Algorithmic phone use heavily distorts inner time perception.",
            expectedOutcome = "Calibrates internal neuro-clock against physical elapsed seconds."
        ),
        LabExperiment(
            id = "exp_reaction",
            code = "EXP 02",
            title = "ATTENTION REFLEX LATENCY",
            domain = "ATTENTION",
            description = "Measure your unprimed reaction speed when an unexpected stimulus appears. Tests nervous system readiness.",
            expectedOutcome = "Evaluates neural processing speed without habitual notification priming."
        ),
        LabExperiment(
            id = "exp_memory",
            code = "EXP 03",
            title = "WORKING MEMORY SPAN",
            domain = "MEMORY",
            description = "Recall a sequential series of geometric numbers. Tests working memory capacity free of notification interruptions.",
            expectedOutcome = "Benchmarks cognitive retention buffer under low-noise conditions."
        ),
        LabExperiment(
            id = "exp_inhibition",
            code = "EXP 04",
            title = "MOTOR INHIBITION (GO / NO-GO)",
            domain = "CHOICE",
            description = "Respond rapidly to green signals but immediately halt motor response on black signals. Tests impulse suppression.",
            expectedOutcome = "Measures executive control over reflexive tapping habits."
        )
    )
}

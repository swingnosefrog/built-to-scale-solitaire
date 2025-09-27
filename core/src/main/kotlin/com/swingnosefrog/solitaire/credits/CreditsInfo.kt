package com.swingnosefrog.solitaire.credits

import com.swingnosefrog.solitaire.Localization
import paintbox.binding.ReadOnlyVar
import java.util.Locale


class CreditsInfo {

    val credits: Map<ReadOnlyVar<String>, List<ReadOnlyVar<String>>> = mapOf(
        Localization["credits.category.projectLeadAndProgramming"] to abcSorted(
            "swingnosefrog"
        ).toVars(),
        Localization["credits.category.graphics"] to abcSorted(
            "Merch_Andise",
            "snow krow",
            "garbo",
        ).toVars(),
        Localization["credits.category.music"] to abcSorted(
            "GENERIC",
        ).toVars(),
        Localization["credits.category.sfx"] to abcSorted(
            "GENERIC", "Kievit", "Merch_Andise",
        ).toVars(),
        Localization["credits.category.testing"] to abcSorted(
            "GENERIC",
            "Luxury",
            "spoopster",
            "Conn",
            "Merch_Andise",
            "Kievit",
            "NoahAmp",
            "snow krow",
            "TheAlternateDoctor",
            "garbo",
            "Huebird",
        ).toVars(),
        Localization["credits.category.resourcesAndTechnologies"] to listOf(
            "libGDX & LWJGL",
            "Kotlin",
            "Java runtime (Temurin)",
            "Paintbox",
            "Beads Sound System",
            "minimal-json",
            "steamworks4j",
            "JCommander",
        ).toVars(),
        Localization["credits.category.fontsUsed"] to abcSorted(
            "Crimson Pro",
            "Outfit",
            "PromptFont",
            "Radio Canada Big",
            "Open Sans",
            "Unifont",
        ).toVars(),
    )

    private fun abcSorted(vararg things: String): List<String> = things.sortedBy { it.lowercase(Locale.ROOT) }
    
    private fun List<String>.toVars(): List<ReadOnlyVar<String>> = this.map { ReadOnlyVar.const(it) }
}
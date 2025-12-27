package com.swingnosefrog.solitaire.credits

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireLocalePicker
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.binding.toConstVar
import paintbox.i18n.NamedLocale
import java.util.*


class CreditsInfo {

    val credits: Map<ReadOnlyVar<String>, List<ReadOnlyVar<String>>> = listOfNotNull(
        Localization["credits.category.projectLeadAndProgramming"] to abcSorted(
            "swingnosefrog"
        ).toVars(),
        Localization["credits.category.graphics"] to abcSorted(
            "Merch_Andise",
            "snow krow",
            "garbo",
        ).toVars(),
        Localization["credits.category.music"] to abcSorted(
            "GenericArrangements",
        ).toVars(),
        Localization["credits.category.sfx"] to abcSorted(
            "GenericArrangements", "Kievit", "Merch_Andise",
        ).toVars(),
        Localization["credits.category.testing"] to abcSorted(
            "GenericArrangements",
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
        if (SolitaireLocalePicker.namedLocales.size <= 1) null
        else Localization["credits.category.localization"] to listOf(
            localizationSubheading(
                SolitaireLocalePicker.namedLocales.first { it.locale.toString() == "it" },
                listOf("Gosh")
            ),
        ),
        Localization["credits.category.specialThanks"] to listOf(
            specialThanksSubheading(
                "credits.category.specialThanks.subheading.logo",
                abcSorted("snow krow")
            ),
            specialThanksSubheading(
                "credits.category.specialThanks.subheading.steamStoreAndLibraryArt",
                abcSorted("Merch_Andise", "snow krow")
            ),
            specialThanksSubheading(
                "credits.category.specialThanks.subheading.steamAchievements",
                abcSorted("Merch_Andise", "Kievit", "spoopster", "Luxury")
            ),
            specialThanksSubheading(
                "credits.category.specialThanks.subheading.steamDeckTesting",
                abcSorted("spoopster", "NoahAmp", "TheAlternateDoctor")
            ),
        ),
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
        ).toVars(),
    ).toMap()

    val otherAttributions: List<ReadOnlyVar<String>> = listOf(
        Localization["credits.otherAttributions.thisGame"],
        Localization["credits.otherAttributions.fontLicenses"],
        Localization["credits.otherAttributions.promptFont"],
    )

    private fun abcSorted(vararg things: String): List<String> = things.sortedBy { it.lowercase(Locale.ROOT) }

    private fun List<String>.toVars(): List<ReadOnlyVar<String>> = this.map { ReadOnlyVar.const(it) }

    private fun localizationSubheading(namedLocale: NamedLocale, names: List<String>): ReadOnlyVar<String> {
        return genericSubheading(
            "credits.category.localization.subheading",
            namedLocale.name.toConstVar(),
            names
        )
    }

    private fun specialThanksSubheading(subheadingKey: String, names: List<String>): ReadOnlyVar<String> {
        return genericSubheading("credits.category.specialThanks.subheading", Localization[subheadingKey], names)
    }

    private fun genericSubheading(
        subheadingFormatKey: String,
        subheading: ReadOnlyVar<String>,
        names: List<String>,
    ): ReadOnlyVar<String> {
        return Localization[subheadingFormatKey, Var {
            listOf(
                subheading.use(),
                names.joinToString(separator = "\n")
            )
        }]
    }
}
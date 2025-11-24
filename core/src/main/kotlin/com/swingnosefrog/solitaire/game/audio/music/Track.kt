package com.swingnosefrog.solitaire.game.audio.music


sealed class Track {

    data object Default : Track() {

        enum class Stems(override val assetKey: String) : StemType {

            DRUMS("music_gameplay_stem_drums"),
            KEYS("music_gameplay_stem_keys"),
            LEAD("music_gameplay_stem_lead"),
            SIDE("music_gameplay_stem_side"),
        }

        override val name: String = "gameplay"
        override val allStemTypes: List<StemType> = Stems.entries.toList()

        override fun getStemMixForScenario(scenario: StemMixScenario): StemMix {
            return when (scenario) {
                StemMixScenario.NONE -> emptySet()
                StemMixScenario.GAMEPLAY -> setOf(Stems.DRUMS, Stems.KEYS, Stems.LEAD, Stems.SIDE)
                StemMixScenario.AFTER_WIN -> setOf(Stems.DRUMS, Stems.SIDE)
            }
        }
    }

    data object Practice : Track() {

        enum class Stems(override val assetKey: String) : StemType {

            BASS("music_tutorial_stem_bass"),
            DRUMS("music_tutorial_stem_drums"),
            KEYS("music_tutorial_stem_keys"),
            SIDE("music_tutorial_stem_side"),
        }

        override val name: String = "practice"
        override val allStemTypes: List<StemType> = Stems.entries.toList()

        override fun getStemMixForScenario(scenario: StemMixScenario): StemMix {
            return when (scenario) {
                StemMixScenario.NONE -> emptySet()
                StemMixScenario.GAMEPLAY -> setOf(Stems.DRUMS, Stems.KEYS, Stems.BASS, Stems.SIDE)
                StemMixScenario.AFTER_WIN -> setOf(Stems.DRUMS, Stems.SIDE)
            }
        }
    }

    abstract val name: String

    abstract val allStemTypes: List<StemType>
    
    abstract fun getStemMixForScenario(scenario: StemMixScenario): StemMix
}
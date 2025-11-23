package com.swingnosefrog.solitaire.game.audio.music


sealed class Track {

    data object Gameplay : Track() {

        enum class GameplayStems(override val assetKey: String) : StemType {

            DRUMS("music_gameplay_stem_drums"),
            KEYS("music_gameplay_stem_keys"),
            LEAD("music_gameplay_stem_lead"),
            SIDE("music_gameplay_stem_side"),
        }

        override val name: String = "gameplay"
        override val allStemTypes: List<StemType> = GameplayStems.entries.toList()

        override fun getStemMixForScenario(scenario: StemMixScenario): StemMix {
            return when (scenario) {
                StemMixScenario.NONE -> emptySet()
                StemMixScenario.GAMEPLAY -> setOf(GameplayStems.DRUMS, GameplayStems.KEYS, GameplayStems.LEAD, GameplayStems.SIDE)
                StemMixScenario.AFTER_WIN -> setOf(GameplayStems.DRUMS, GameplayStems.SIDE)
            }
        }
    }

    data object Practice : Track() {

        enum class PracticeStems(override val assetKey: String) : StemType {

            BASS("music_tutorial_stem_bass"),
            DRUMS("music_tutorial_stem_drums"),
            KEYS("music_tutorial_stem_keys"),
            SIDE("music_tutorial_stem_side"),
        }

        override val name: String = "practice"
        override val allStemTypes: List<StemType> = PracticeStems.entries.toList()

        override fun getStemMixForScenario(scenario: StemMixScenario): StemMix {
            return when (scenario) {
                StemMixScenario.NONE -> emptySet()
                StemMixScenario.GAMEPLAY -> setOf(PracticeStems.DRUMS, PracticeStems.KEYS, PracticeStems.BASS, PracticeStems.SIDE)
                StemMixScenario.AFTER_WIN -> setOf(PracticeStems.DRUMS, PracticeStems.SIDE)
            }
        }
    }

    abstract val name: String

    abstract val allStemTypes: List<StemType>
    
    abstract fun getStemMixForScenario(scenario: StemMixScenario): StemMix
}
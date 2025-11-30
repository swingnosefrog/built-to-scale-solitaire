package com.swingnosefrog.solitaire.game.audio.music

import com.swingnosefrog.solitaire.game.audio.FoundationNoteProvider


sealed class Track : FoundationNoteProvider {

    data object Classic : Track() {

        enum class Stems(override val assetKey: String) : StemType {

            DRUMS("music_classic_stem_drums"),
            KEYS("music_classic_stem_keys"),
            LEAD("music_classic_stem_lead"),
            SIDE("music_classic_stem_side"),
        }

        override val name: String = "classic"
        override val allStemTypes: List<StemType> = Stems.entries.toList()

        override val notesAssetKeys: List<String> = listOf(
            // Foundation
            "sfx_game_note_G2",
            "sfx_game_note_A2",
            "sfx_game_note_As2",
            "sfx_game_note_C3",
            "sfx_game_note_D3",
            "sfx_game_note_Ds3",
            "sfx_game_note_F3",
            "sfx_game_note_G3",
            // Widget
            "sfx_game_note_G2",
            "sfx_game_note_D3",
            "sfx_game_note_G3",
        )

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
        
        override val notesAssetKeys: List<String> = listOf(
            // Foundation
            "sfx_game_note_Cs3",
            "sfx_game_note_Ds3",
            "sfx_game_note_F3",
            "sfx_game_note_Fs3",
            "sfx_game_note_Gs3",
            "sfx_game_note_As3",
            "sfx_game_note_C4",
            "sfx_game_note_Cs4",
            // Widget
            "sfx_game_note_Cs3",
            "sfx_game_note_Gs3",
            "sfx_game_note_Cs4",
        )

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
package com.swingnosefrog.solitaire.game.audio


interface FoundationNoteProvider {

    /**
     * List of size 11 containing asset keys for sounds.
     * First 8 items are the notes used for building the foundation. Last 3 items is the chord for a widget assemble.
     */
    val notesAssetKeys: List<String>


    data object Default : FoundationNoteProvider {

        override val notesAssetKeys: List<String> = listOf(
            // Foundation
            "sfx_game_note_C3",
            "sfx_game_note_D3",
            "sfx_game_note_E3",
            "sfx_game_note_F3",
            "sfx_game_note_G3",
            "sfx_game_note_A3",
            "sfx_game_note_B3",
            "sfx_game_note_C4",
            // Widget
            "sfx_game_flick_note_C3",
            "sfx_game_flick_note_G3",
            "sfx_game_flick_note_C4",
        )
    }
}

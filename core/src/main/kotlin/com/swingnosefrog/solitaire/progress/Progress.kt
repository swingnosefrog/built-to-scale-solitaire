package com.swingnosefrog.solitaire.progress

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.files.FileHandle
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.persistence.GameSaveLocationHelper
import com.swingnosefrog.solitaire.settings.PreferenceKeys
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.prefs.KeyValue
import paintbox.prefs.NewIndicator
import paintbox.prefs.PaintboxPreferences


class Progress(game: SolitaireGame, prefs: Preferences) : PaintboxPreferences<SolitaireGame>(game, prefs) {

    companion object {

        fun getDefaultFileLocation(): FileHandle =
            FileHandle(GameSaveLocationHelper.saveDirectory.resolve("progress.${GameSaveLocationHelper.SAVE_FILE_EXTENSION}"))
    }

    override val allKeyValues: List<KeyValue<*>>
    override val allNewIndicators: List<NewIndicator>

    val unlockedMusicTrackChanging: BooleanVar
    val unlockedCardSkinChanging: ReadOnlyBooleanVar

    init {
        val initScope = InitScope()
        with(initScope) {
            unlockedMusicTrackChanging = KeyValue.Bool("unlockedMusicTrackChanging", false).add().value
            unlockedCardSkinChanging = BooleanVar(unlockedMusicTrackChanging)
        }

        allKeyValues = initScope.allKeyValues.toList()
        allNewIndicators = initScope.allNewIndicators.toList()
    }

    override fun getLastVersionKey(): String = PreferenceKeys.LAST_VERSION
}

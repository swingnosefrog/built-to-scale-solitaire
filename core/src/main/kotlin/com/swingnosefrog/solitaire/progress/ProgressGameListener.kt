package com.swingnosefrog.solitaire.progress

import com.swingnosefrog.solitaire.game.logic.GameEventListener
import com.swingnosefrog.solitaire.game.logic.GameLogic


class ProgressGameListener(
    private val progress: Progress,
) : GameEventListener.Adapter() {

    override fun onGameWon(gameLogic: GameLogic) {
        val progress = progress
        if (!progress.unlockedMusicTrackChanging.get()) {
            progress.unlockedMusicTrackChanging.set(true)
            try {
                progress.persist()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
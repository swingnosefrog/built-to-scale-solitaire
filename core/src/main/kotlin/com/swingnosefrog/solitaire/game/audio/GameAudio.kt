package com.swingnosefrog.solitaire.game.audio

import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.soundsystem.SoundSystem


class GameAudio(
    val gameLogic: GameLogic,
    val soundSystem: SoundSystem,
    val music: GameMusic?
) : Disposable {
    
    val eventListener: GameAudioEventListener = GameAudioEventListener(this)
    
    init {
        gameLogic.eventDispatcher.addListener(eventListener)
        
        soundSystem.startRealtime()
    }
    
    override fun dispose() {
        eventListener.dispose()
        gameLogic.eventDispatcher.removeListener(eventListener)
    }
}
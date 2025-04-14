package com.swingnosefrog.solitaire.game.audio

import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.soundsystem.SoundSystem


class GameAudio(
    val gameLogic: GameLogic,
    val soundSystem: SoundSystem,
    private val ownsSoundSystem: Boolean,
) : Disposable {
    
    
    val music: GameMusic = GameMusic(this)
    
    val eventListener: GameAudioEventListener = GameAudioEventListener(this)
    
    init {
        gameLogic.eventDispatcher.addListener(eventListener)
        
        soundSystem.startRealtime()
    }
    
    override fun dispose() {
        eventListener.dispose()
        gameLogic.eventDispatcher.removeListener(eventListener)
        
        music.dispose()
        
        if (ownsSoundSystem) soundSystem.dispose()
    }
}
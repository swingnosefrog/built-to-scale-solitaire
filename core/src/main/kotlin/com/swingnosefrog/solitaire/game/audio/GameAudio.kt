package com.swingnosefrog.solitaire.game.audio

import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.soundsystem.SoundSystem
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsAudio
import com.swingnosefrog.solitaire.soundsystem.sample.PlayerLike
import net.beadsproject.beads.ugens.Gain


class GameAudio(
    val gameLogic: GameLogic,
    private val soundSystem: SoundSystem,
    val music: GameMusic?,
) : Disposable {

    val eventListener: GameAudioEventListener = GameAudioEventListener(this)

    private val gain: Gain = Gain(soundSystem.audioContext, 2, 1f)

    init {
        gameLogic.eventDispatcher.addListener(eventListener)
        
        soundSystem.audioContext.out.addInput(this.gain)
    }

    fun playAudio(beadsAudio: BeadsAudio, callback: (player: PlayerLike) -> Unit = {}): Long {
        return soundSystem.playAudio(beadsAudio, this.gain, callback)
    }
    
    fun getPlayerOrNull(id: Long): PlayerLike? = soundSystem.getPlayerOrNull(id)

    override fun dispose() {
        eventListener.dispose()
        gameLogic.eventDispatcher.removeListener(eventListener)
        soundSystem.audioContext.out.removeAllConnections(gain)
    }
}
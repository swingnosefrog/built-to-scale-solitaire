package com.swingnosefrog.solitaire.game.audio

import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.soundsystem.SoundSystem
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsAudio
import com.swingnosefrog.solitaire.soundsystem.sample.PlayerLike
import net.beadsproject.beads.ugens.Gain
import paintbox.binding.FloatVar
import paintbox.binding.ReadOnlyFloatVar


class GameAudio(
    val gameLogic: GameLogic,
    private val soundSystem: SoundSystem,
    val music: GameMusic?,
) : Disposable {

    val eventListener: GameAudioEventListener = GameAudioEventListener(this)

    private val sfxGain: ReadOnlyFloatVar = FloatVar { SolitaireGame.globalVolumeGain.sfxVolumeGain.use() }
    
    private val centralUgen: Gain = Gain(soundSystem.audioContext, 2, 1f)
    private val sfxGainUgen: Gain = Gain(soundSystem.audioContext, 2, sfxGain.get())

    init {
        gameLogic.eventDispatcher.addListener(eventListener)
        
        sfxGain.addListener { listener ->
            sfxGainUgen.gain = listener.getOrCompute()
        }
        centralUgen.addInput(sfxGainUgen)
        
        soundSystem.audioContext.out.addInput(this.centralUgen)
    }

    fun getPlayerOrNull(id: Long): PlayerLike? = soundSystem.getPlayerOrNull(id)

    fun playSfx(beadsAudio: BeadsAudio, callback: (player: PlayerLike) -> Unit = {}): Long {
        return soundSystem.playAudio(beadsAudio, this.sfxGainUgen, callback)
    }

    override fun dispose() {
        eventListener.dispose()
        gameLogic.eventDispatcher.removeListener(eventListener)
        soundSystem.audioContext.out.removeAllConnections(centralUgen)
    }
}
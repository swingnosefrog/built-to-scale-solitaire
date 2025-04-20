package com.swingnosefrog.solitaire.game.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.soundsystem.SoundSystem
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsSound
import com.swingnosefrog.solitaire.soundsystem.sample.PlayerLike
import net.beadsproject.beads.core.AudioContext
import net.beadsproject.beads.ugens.Gain
import net.beadsproject.beads.ugens.SamplePlayer
import paintbox.binding.FloatVar
import paintbox.binding.ReadOnlyFloatVar
import paintbox.util.gdxutils.GdxDelayedRunnable
import paintbox.util.gdxutils.GdxRunnableTransition
import java.util.EnumSet


typealias StemMix = EnumSet<GameMusic.StemType>

class GameMusic(soundSystem: SoundSystem) : Disposable {
    
    enum class StemType(val assetKey: String) {
        DRUMS("music_gameplay_stem_drums"),
        KEYS("music_gameplay_stem_keys"),
        LEAD("music_gameplay_stem_lead"),
        SIDE("music_gameplay_stem_side"),
    }
    
    object StemMixes {
        
        val ALL: StemMix = EnumSet.allOf(StemType::class.java)
        val AFTER_WIN: StemMix = EnumSet.of(StemType.DRUMS, StemType.SIDE)
    }

    private val stemAudio: Map<StemType, BeadsSound> = StemType.entries.associateWith { type ->
        GameAssets.get<BeadsSound>(type.assetKey)
    }

    private val audioContext: AudioContext = soundSystem.audioContext
    private val musicAttenuationMultiplier: FloatVar = FloatVar(1f)
    private val musicGain: ReadOnlyFloatVar =
        FloatVar { SolitaireGame.globalVolumeGain.musicVolumeGain.use() * musicAttenuationMultiplier.use() }
    private val commonUgen: Gain = Gain(audioContext, 2, musicGain.get())

    private var currentStemMix: StemMix = StemMixes.ALL
    private val stemPlayers: Map<StemType, PlayerLike>
    private var currentStemMixTransition: GdxRunnableTransition? = null

    init {
        stemPlayers = stemAudio.entries.associate { (stemType, beadsSound) ->
            val sample = beadsSound.sample
            val player = beadsSound.createPlayer(audioContext)
            player.loopStartMs = 0f
            player.loopEndMs = sample.length.toFloat()
            player.loopType = SamplePlayer.LoopType.LOOP_FORWARDS
            
            player.position = -150.0 // ms, avoids slight stutter when buffer is filling

            commonUgen.addInput(player)

            stemType to player
        }
        
        musicGain.addListener { listener ->
            val newGain = listener.getOrCompute()
            commonUgen.gain = newGain
        }

        audioContext.out.addInput(commonUgen)
    }
    
    fun transitionToStemMix(newStemMix: StemMix, transitionTimeSec: Float) {
        val oldStemMix = currentStemMix
        if (oldStemMix == newStemMix) return
        
        val oldStemMixTransition = currentStemMixTransition
        if (oldStemMixTransition != null) {
            if (!oldStemMixTransition.isCompleted()) {
                oldStemMixTransition.cancel(setToEndValue = false)
            }
            currentStemMixTransition = null
        }
        
        data class StartEndGain(val startingGain: Float, val endingGain: Float)
        
        val startEndGains = stemPlayers.entries.associate { (stemType, player) ->
            val endingGain = if (stemType in newStemMix) 1f else 0f
            stemType to StartEndGain(player.gain, endingGain)
        }
        val interpolation = Interpolation.linear
        val newTransition = GdxRunnableTransition(0f, 1f, transitionTimeSec) { _, progress ->
            for ((stemType, startingGainObj) in startEndGains.entries) {
                stemPlayers.getValue(stemType).gain =
                    interpolation.apply(startingGainObj.startingGain, startingGainObj.endingGain, progress)
            }
        }
        currentStemMixTransition = newTransition
        Gdx.app.postRunnable(newTransition)
        
        this.currentStemMix = newStemMix
    }

    fun attenuateMusicForGameWinSfx(
        sfxDuration: Float = 3f,
        softenedGain: Float = 0.2f,
        attenuationTransitionSec: Float = 0.125f,
        resumeTransitionSec: Float = 1f,
    ) {
        val multiplierVar = musicAttenuationMultiplier
        
        Gdx.app.postRunnable(GdxRunnableTransition(multiplierVar.get(), softenedGain, attenuationTransitionSec) { v, _ ->
            multiplierVar.set(v)
        })

        Gdx.app.postRunnable(GdxDelayedRunnable(sfxDuration) {
            Gdx.app.postRunnable(GdxRunnableTransition(softenedGain, 1f, resumeTransitionSec) { v, _ ->
                multiplierVar.set(v)
            })
        })
    }

    override fun dispose() {
        audioContext.out.removeAllConnections(commonUgen)
        commonUgen.pause(true)
    }
}
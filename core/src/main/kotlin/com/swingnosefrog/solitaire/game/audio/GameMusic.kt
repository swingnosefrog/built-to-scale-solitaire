package com.swingnosefrog.solitaire.game.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.soundsystem.SoundSystem
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsSound
import com.swingnosefrog.solitaire.soundsystem.sample.PlayerLike
import net.beadsproject.beads.core.AudioContext
import net.beadsproject.beads.ugens.Gain
import net.beadsproject.beads.ugens.SamplePlayer
import paintbox.binding.FloatVar
import paintbox.util.gdxutils.GdxDelayedRunnable
import paintbox.util.gdxutils.GdxRunnableTransition


class GameMusic(soundSystem: SoundSystem) : Disposable {

    private enum class StemType(val assetKey: String) {
        DRUMS("music_gameplay_stem_drums"),
        KEYS("music_gameplay_stem_keys"),
        LEAD("music_gameplay_stem_lead"),
        SIDE("music_gameplay_stem_side"),
    }

    private val stemAudio: Map<StemType, BeadsSound> = StemType.entries.associateWith { type ->
        GameAssets.get<BeadsSound>(type.assetKey)
    }

    private val audioContext: AudioContext = soundSystem.audioContext
    val musicGainMultiplier: FloatVar = FloatVar(1f)
    private val commonUgen: Gain = Gain(audioContext, 2, musicGainMultiplier.get())

    private val stemPlayers: Map<StemType, PlayerLike>
    

    init {
        stemPlayers = stemAudio.entries.associate { (stemType, beadsSound) ->
            val sample = beadsSound.sample
            val player = beadsSound.createPlayer(audioContext)
            player.loopStartMs = 0f
            player.loopEndMs = sample.length.toFloat()
            player.loopType = SamplePlayer.LoopType.LOOP_FORWARDS
            
            player.position = -100.0 // ms, avoids slight stutter when buffer is filling

            commonUgen.addInput(player)

            stemType to player
        }
        
        musicGainMultiplier.addListener { listener ->
            val newGain = listener.getOrCompute()
            commonUgen.gain = newGain
        }

        audioContext.out.addInput(commonUgen)
    }

    fun attenuateForGameWinSfx(
        sfxDuration: Float = 3f,
        softenedGain: Float = 0.2f,
        attenuationTransitionSec: Float = 0.125f,
        resumeTransitionSec: Float = 1f,
    ) {
        val multiplierVar = musicGainMultiplier
        
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
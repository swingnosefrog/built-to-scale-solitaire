package com.swingnosefrog.solitaire.game.audio.music

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.game.logic.GameEventListener
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.soundsystem.SoundSystem
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsSound
import com.swingnosefrog.solitaire.soundsystem.sample.PlayerLike
import net.beadsproject.beads.core.AudioContext
import net.beadsproject.beads.ugens.Gain
import net.beadsproject.beads.ugens.SamplePlayer
import paintbox.binding.FloatVar
import paintbox.binding.ReadOnlyFloatVar
import paintbox.util.gdxutils.GdxRunnableTransition


class GameMusic(soundSystem: SoundSystem) : Disposable {

    private val allTracks: List<Track> = listOf(Track.Default, Track.Practice)
    private val stemAudio: Map<StemType, BeadsSound> =
        allTracks.flatMap { it.allStemTypes }.distinct().associateWith { type ->
            GameAssets.get<BeadsSound>(type.assetKey)
        }

    private val audioContext: AudioContext = soundSystem.audioContext
    private val musicAttenuationMultiplier: FloatVar = FloatVar(1f)
    private val musicGain: ReadOnlyFloatVar =
        FloatVar { SolitaireGame.globalVolumeGain.musicVolumeGain.use() * musicAttenuationMultiplier.use() }
    private val commonUgen: Gain = Gain(audioContext, 2, musicGain.get())

    private var currentStemMixScenario: StemMixScenario = StemMixScenario.NONE
    private var currentTrack: Track = Track.Practice // TODO change
    private var currentStemMix: StemMix = emptySet()
    private var currentStemMixTransition: GdxRunnableTransition.State? = null
    private val stemPlayers: Map<StemType, PlayerLike>

    val gameEventListener: GameEventListener = this.GameListener()

    init {
        val allAudio = stemAudio.entries
        stemPlayers = allAudio.associate { (stemType, beadsSound) ->
            val sample = beadsSound.sample
            val player = beadsSound.createPlayer(audioContext)
            player.loopStartMs = 0f
            player.loopEndMs = sample.length.toFloat()
            player.loopType = SamplePlayer.LoopType.LOOP_FORWARDS

            player.position = -150.0 // ms, avoids slight stutter when buffer is filling

            player.gain = 0f

            commonUgen.addInput(player)

            stemType to player
        }

        musicGain.addListener { listener ->
            val newGain = listener.getOrCompute()
            commonUgen.gain = newGain
        }

        audioContext.out.addInput(commonUgen)

        transitionToStemMix(0.2f, StemMixScenario.GAMEPLAY)
    }

    fun transitionToStemMix(transitionTimeSec: Float, scenario: StemMixScenario, track: Track? = null) {
        if (track != null) {
            currentTrack = track
        }
        currentStemMixScenario = scenario

        val stemMix = currentTrack.getStemMixForScenario(scenario)
        transitionToStemMix(stemMix, transitionTimeSec)
    }

    private fun transitionToStemMix(newStemMix: StemMix, transitionTimeSec: Float) {
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
        }.toRunnable()
        currentStemMixTransition = newTransition
        Gdx.app.postRunnable(newTransition)

        this.currentStemMix = newStemMix
    }

    override fun dispose() {
        audioContext.out.removeAllConnections(commonUgen)
        commonUgen.pause(true)
    }


    private inner class GameListener : GameEventListener.Adapter() {

        override fun onGameWon(gameLogic: GameLogic) {
            transitionToStemMix(0.5f, StemMixScenario.AFTER_WIN) // TODO change track during gameplay point
            attenuateMusicForGameWinSfx()
        }

        private fun attenuateMusicForGameWinSfx(
            sfxDuration: Float = 3f,
            softenedGain: Float = 0.2f,
            attenuationTransitionSec: Float = 0.125f,
            resumeTransitionSec: Float = 1f,
        ) {
            val multiplierVar = musicAttenuationMultiplier

            Gdx.app.postRunnable(
                GdxRunnableTransition(
                    startValue = multiplierVar.get(),
                    endValue = softenedGain,
                    durationSec = attenuationTransitionSec
                ) { v, _ ->
                    multiplierVar.set(v)
                }.toRunnable()
            )

            Gdx.app.postRunnable(
                GdxRunnableTransition(
                    startValue = softenedGain, endValue = 1f, durationSec = resumeTransitionSec, delaySec = sfxDuration,
                ) { v, _ ->
                    multiplierVar.set(v)
                }.toRunnable()
            )
        }
    }
}
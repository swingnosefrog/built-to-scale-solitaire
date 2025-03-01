package com.swingnosefrog.solitaire.soundsystem

import com.badlogic.gdx.utils.Disposable
import net.beadsproject.beads.core.AudioContext
import net.beadsproject.beads.core.IOAudioFormat
import net.beadsproject.beads.core.UGen
import com.swingnosefrog.solitaire.soundsystem.sample.PlayerLike
import java.util.concurrent.ConcurrentHashMap


/**
 * A wrapper for an [net.beadsproject.beads.core.AudioContext] and associated audio system utilities.
 */
class SoundSystem(
    val realtimeOutput: RealtimeOutput,
    initCtxBufferSize: Int = AudioContext.DEFAULT_BUFFER_SIZE,
    val settings: SoundSystemSettings = SoundSystemSettings(),
) : Disposable {

    companion object {

        val AUDIO_FORMAT_44100: IOAudioFormat = IOAudioFormat(44_100f, 16, 2, 2, true, true)
        val AUDIO_FORMAT_48000: IOAudioFormat = IOAudioFormat(48_000f, 16, 2, 2, true, true)
        val DEFAULT_AUDIO_FORMAT: IOAudioFormat = AUDIO_FORMAT_48000

        fun createDefaultSoundSystem(
            initCtxBufferSize: Int = AudioContext.DEFAULT_BUFFER_SIZE,
            settings: SoundSystemSettings = SoundSystemSettings(),
        ): SoundSystem {
            return SoundSystem(RealtimeOutput.OpenAL(AudioDeviceSettings.DEFAULT_SETTINGS), initCtxBufferSize, settings)
        }

    }

    data class SoundSystemSettings(val ioAudioFormat: IOAudioFormat = DEFAULT_AUDIO_FORMAT)

    val audioContext: AudioContext =
        AudioContext(realtimeOutput.createAudioIO(), initCtxBufferSize, settings.ioAudioFormat)

    @Volatile
    private var currentlyRealTime: Boolean = true

    @Volatile
    private var currentSoundID: Long = 0L
    private val activePlayers: MutableMap<Long, PlayerLike> = ConcurrentHashMap()

    @Volatile
    private var isDisposed: Boolean = false

    val isPaused: Boolean
        get() = audioContext.out.isPaused

    init {
        audioContext.out.pause(true)
    }

    fun setPaused(paused: Boolean) {
        audioContext.out.pause(paused)
    }

    fun startRealtime() {
        currentlyRealTime = true
        audioContext.start()
    }

    fun stopRealtime() {
        audioContext.stop()
        currentlyRealTime = false
    }

    fun startNonrealtimeTimed(msToRun: Double) {
        currentlyRealTime = false
        audioContext.runForNMillisecondsNonRealTime(msToRun)
    }

    fun isCurrentlyRealtime(): Boolean = currentlyRealTime

    override fun dispose() {
        isDisposed = true
        stopRealtime()
        audioContext.out.clearInputConnections()
        audioContext.out.clearDependents()
    }

    private fun obtainSoundID(): Long = ++currentSoundID

    /**
     * Plays audio and returns the sound ID.
     * @param addInputTo If null, defaults to [audioContext].out
     */
    fun playAudio(beadsAudio: BeadsAudio, addInputTo: UGen? = null, callback: (player: PlayerLike) -> Unit = {}): Long {
        val id = obtainSoundID()
        val player = beadsAudio.createPlayer(audioContext)
        player.killListeners += {
            activePlayers.remove(id, it)
        }
        callback.invoke(player)
        activePlayers[id] = player
        (addInputTo ?: audioContext.out).addInput(player)
        return id
    }

    fun getPlayer(id: Long): PlayerLike? = activePlayers[id]

}